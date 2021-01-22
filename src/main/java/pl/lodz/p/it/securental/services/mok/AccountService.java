package pl.lodz.p.it.securental.services.mok;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.mok.AccountAdapter;
import pl.lodz.p.it.securental.adapters.mok.OtpCredentialsAdapter;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.mappers.mok.AccountMapper;
import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.dto.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.dto.mok.RegistrationResponse;
import pl.lodz.p.it.securental.entities.mok.*;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.QrCodeGenerationException;
import pl.lodz.p.it.securental.exceptions.mok.UsernameNotMatchingException;
import pl.lodz.p.it.securental.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class AccountService {

    private final AccountAdapter accountAdapter;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthenticator googleAuthenticator;
    private final OtpCredentialsAdapter otpCredentialsAdapter;
    private final EmailSender emailSender;
    private final SignatureUtils signatureUtils;
    private final AccountMapper accountMapper;

    private List<MaskedPassword> generateMaskedPasswords(String fullPassword) {
        List<MaskedPassword> maskedPasswords = new ArrayList<>();
        int[] maskedPasswordLengthRange = IntStream.rangeClosed(
                ApplicationProperties.MASKED_PASSWORD_MIN_LENGTH,
                ApplicationProperties.MASKED_PASSWORD_MAX_LENGTH)
                .toArray();

        for (int maskedPasswordLength : maskedPasswordLengthRange) {
            Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(ApplicationProperties.FULL_PASSWORD_LENGTH, maskedPasswordLength);
            while (iterator.hasNext()) {
                int[] combination = iterator.next();
                MaskedPassword maskedPassword = new MaskedPassword();
                maskedPassword.setCombination(passwordEncoder.encode(StringUtils.integerArrayToString(combination)));
                maskedPassword.setHash(passwordEncoder.encode(StringUtils.selectCharacters(fullPassword, combination)));
                maskedPasswords.add(maskedPassword);
            }
        }
        return maskedPasswords;
    }

    public RegistrationResponse addAccount(AccountDto accountDto, String language) throws ApplicationBaseException {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(accountDto.getUsername());
        String lastPasswordCharacters = generateLastPasswordCharacters();
        Account account = AccountMapper.toAccount(accountDto);
        account.setConfirmed(true);
        account.setCredentials(new Credentials(generateMaskedPasswords(accountDto.getPassword() + lastPasswordCharacters)));
        account.setAuthenticationToken(new AuthenticationToken(new ArrayList<>(), LocalDateTime.now()));
        if (otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).isPresent()) {
            account.setOtpCredentials(otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).get());
            String subject = StringUtils.getTranslatedText("created.subject", language);
            String text = StringUtils.getTranslatedText("confirm.text", language) + "<img src='data:image/png;base64," + generateQrCode(accountDto.getUsername(), key) + "'/>";
            emailSender.sendMessage(account.getEmail(), subject, text);
        } else {
            throw new AccountNotFoundException();
        }
        accountAdapter.addAccount(account);
        return RegistrationResponse.builder()
                .lastPasswordCharacters(lastPasswordCharacters)
                .build();
    }

    public RegistrationResponse register(AccountDto accountDto, String language) throws ApplicationBaseException {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(accountDto.getUsername());
        String lastPasswordCharacters = generateLastPasswordCharacters();
        Account account = AccountMapper.toAccount(accountDto);
        account.setActive(true);
        account.setConfirmed(false);
        account.setConfirmationToken(StringUtils.randomBase64Url());
        account.setCredentials(new Credentials(generateMaskedPasswords(accountDto.getPassword() + lastPasswordCharacters)));
        account.setAuthenticationToken(new AuthenticationToken(new ArrayList<>(), LocalDateTime.now()));
        account.setAccessLevels(generateClientAccessLevels(account));
        if (otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).isPresent()) {
            account.setOtpCredentials(otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).get());
            String subject = StringUtils.getTranslatedText("confirm.subject", language);
            String text = "<a href=\"" + ApplicationProperties.FRONTEND_ORIGIN + "/confirmAccount/" + account.getConfirmationToken() + "\">"
                    + StringUtils.getTranslatedText("confirm.link", language) + "</a>" + StringUtils.getTranslatedText("confirm.text", language);
            emailSender.sendMessage(account.getEmail(), subject, text);
        } else {
            throw new AccountNotFoundException();
        }
        accountAdapter.addAccount(account);
        return RegistrationResponse.builder()
                .qrCode(generateQrCode(accountDto.getUsername(), key))
                .lastPasswordCharacters(lastPasswordCharacters)
                .build();
    }

    private String generateLastPasswordCharacters() {
        StringBuilder characters = new StringBuilder();
        for (int i = 0; i < ApplicationProperties.LAST_PASSWORD_CHARACTERS_LENGTH; i++) {
            characters.append(StringUtils.randomChar(ApplicationProperties.LAST_PASSWORD_CHARACTERS));
        }
        return characters.toString();
    }

    private List<AccessLevel> generateClientAccessLevels(Account account) {
        List<AccessLevel> accessLevels = new ArrayList<>();
        accessLevels.add((new Admin(ApplicationProperties.ACCESS_LEVEL_ADMIN, false, account)));
        accessLevels.add((new Employee(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE, false, account)));
        accessLevels.add((new Client(ApplicationProperties.ACCESS_LEVEL_CLIENT, true, account)));
        return accessLevels;
    }

    public void confirmAccount(String token) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccountByConfirmationToken(token);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setConfirmed(true);
        } else {
            throw new AccountNotFoundException();
        }
    }

    public AccountDto getAccount(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            return accountMapper.toAccountDtoWithSignature(accountOptional.get());
        } else {
            throw new AccountNotFoundException();
        }
    }

    public List<Integer> initializeLogin(String username) throws ApplicationBaseException {
        List<Integer> randomCombination = StringUtils.randomCombination(
                ApplicationProperties.FULL_PASSWORD_LENGTH,
                ApplicationProperties.MASKED_PASSWORD_MIN_LENGTH,
                ApplicationProperties.MASKED_PASSWORD_MAX_LENGTH);
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            AuthenticationToken authenticationToken = account.getAuthenticationToken();
            authenticationToken.setCombination(randomCombination);
            authenticationToken.setExpiration(LocalDateTime.now().plusMinutes(ApplicationProperties.AUTHENTICATION_TOKEN_EXPIRATION));
        }
        return randomCombination;
    }

    public Page<AccountDto> getAllAccounts(PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return AccountMapper.toAccountDtos(accountAdapter.getAllAccounts(pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return AccountMapper.toAccountDtos(accountAdapter.getAllAccounts(pagingHelper.withoutSorting()));
        }
    }

    public Page<AccountDto> filterAccounts(String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return AccountMapper.toAccountDtos(accountAdapter.filterAccounts(filter, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return AccountMapper.toAccountDtos(accountAdapter.filterAccounts(filter, pagingHelper.withoutSorting()));
        }
    }

    public void editAccount(String username, AccountDto accountDto) throws ApplicationBaseException {
        if (username.equals(accountDto.getUsername())) {
            Optional<Account> accountOptional = accountAdapter.getAccount(username);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (signatureUtils.verify(account.toSignString(), accountDto.getSignature())) {
                    account.setFirstName(accountDto.getFirstName());
                    account.setLastName(accountDto.getLastName());
                    account.setActive(accountDto.isActive());
                    AccountMapper.updateAccessLevels(account.getAccessLevels(), accountDto.getAccessLevels());
                } else {
                    throw new ApplicationOptimisticLockException();
                }
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new UsernameNotMatchingException();
        }
    }

    public void editOwnAccount(String username, AccountDto accountDto) throws ApplicationBaseException {
        if (username.equals(accountDto.getUsername())) {
            Optional<Account> accountOptional = accountAdapter.getAccount(username);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (signatureUtils.verify(account.toSignString(), accountDto.getSignature())) {
                    account.setFirstName(accountDto.getFirstName());
                    account.setLastName(accountDto.getLastName());
                } else {
                    throw new ApplicationOptimisticLockException();
                }
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new UsernameNotMatchingException();
        }
    }

    public AuthenticationResponse.AuthenticationResponseBuilder currentUser(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            List<String> accessLevels = getUserFrontendRoles(account);
            return AuthenticationResponse.builder()
                    .username(username)
                    .accessLevels(accessLevels)
                    .currentAccessLevel(getHighestFrontendRole(accessLevels));
        } else {
            throw new AccountNotFoundException();
        }
    }

    private List<String> getUserFrontendRoles(Account account) {
        return account.getAccessLevels().stream()
                .filter(AccessLevel::isActive)
                .map(AccessLevel::getName)
                .sorted(Comparator.comparing(ApplicationProperties.ACCESS_LEVEL_ORDER::indexOf))
                .collect(Collectors.toList());
    }

    private String getHighestFrontendRole(List<String> roles) {
        if (roles.contains(ApplicationProperties.ACCESS_LEVEL_ADMIN)) {
            return ApplicationProperties.ACCESS_LEVEL_ADMIN;
        } else if (roles.contains(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE)) {
            return ApplicationProperties.ACCESS_LEVEL_EMPLOYEE;
        } else {
            return ApplicationProperties.ACCESS_LEVEL_CLIENT;
        }
    }

    private String generateQrCode(String username, GoogleAuthenticatorKey key) throws ApplicationBaseException {
        byte[] qrCode;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BitMatrix bitMatrix = qrCodeWriter.encode(GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("Securental", username, key), BarcodeFormat.QR_CODE, 200, 200);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            qrCode = outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new QrCodeGenerationException(e);
        }
        return StringUtils.encodeBase64(qrCode);
    }
}
