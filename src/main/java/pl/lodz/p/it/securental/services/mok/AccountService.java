package pl.lodz.p.it.securental.services.mok;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.LogAdapter;
import pl.lodz.p.it.securental.adapters.mok.AccountAdapter;
import pl.lodz.p.it.securental.adapters.mok.OtpCredentialsAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.mappers.mok.AccountMapper;
import pl.lodz.p.it.securental.dto.model.log.LogDto;
import pl.lodz.p.it.securental.dto.model.mok.AccountDto;
import pl.lodz.p.it.securental.dto.model.mok.ChangePasswordRequest;
import pl.lodz.p.it.securental.dto.model.mok.RegistrationResponse;
import pl.lodz.p.it.securental.entities.mok.*;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.*;
import pl.lodz.p.it.securental.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@RequiresNewTransaction
@Retryable(DatabaseConnectionException.class)
public class AccountService {

    private final AccountAdapter accountAdapter;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthenticator googleAuthenticator;
    private final OtpCredentialsAdapter otpCredentialsAdapter;
    private final LogAdapter logAdapter;
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

    private List<MaskedPassword> generateNewMaskedPasswords(String fullPassword, List<MaskedPassword> previous) throws ApplicationBaseException {
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
                String combinationString = StringUtils.integerArrayToString(combination);
                String selectedCharacters = StringUtils.selectCharacters(fullPassword, combination);
                for (MaskedPassword previousMaskedPassword : previous) {
                    if (passwordEncoder.matches(combinationString, previousMaskedPassword.getCombination())) {
                        if (passwordEncoder.matches(selectedCharacters, previousMaskedPassword.getHash())) {
                            throw new PasswordAlreadyUsedException();
                        } else {
                            break;
                        }
                    }
                }
                maskedPassword.setCombination(passwordEncoder.encode(combinationString));
                maskedPassword.setHash(passwordEncoder.encode(selectedCharacters));
                maskedPasswords.add(maskedPassword);
            }
        }
        return maskedPasswords;
    }

    //@PreAuthorize("hasAuthority('resendConfirmationEmail')")
    public void sendConfirmationEmail(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            String language = account.getPreferredLanguage();
            String subject = StringUtils.getTranslatedText("confirm.subject", language);
            String text = "<a href=\"" + ApplicationProperties.FRONTEND_ORIGIN + "/confirmAccount/" + account.getConfirmationToken() + "\">"
                    + StringUtils.getTranslatedText("common.link", language) + "</a>" + StringUtils.getTranslatedText("confirm.text", language);
            emailSender.sendMessage(account.getEmail(), subject, text);
        } else {
            throw new AccountNotFoundException();
        }
    }

    //TODO
    //@PreAuthorize("hasAuthority('resendQrCodeEmail')")
    public void sendQrCodeEmail(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            String language = account.getPreferredLanguage();
            String subject = StringUtils.getTranslatedText("qrCode.subject", language);
            String text = StringUtils.getTranslatedText("created.text", language) + "<img src='data:image/png;base64," + regenerateQrCode(username).getQrCode() + "'/>";
            emailSender.sendMessage(account.getEmail(), subject, text);
        } else {
            throw new AccountNotFoundException();
        }
    }

    //@PreAuthorize("hasAuthority('addAccount')")
    public RegistrationResponse addAccount(AccountDto accountDto, String language) throws ApplicationBaseException {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(accountDto.getUsername());
        String lastPasswordCharacters = generateLastPasswordCharacters();
        Account account = AccountMapper.toAccount(accountDto);
        account.setPreferredLanguage(language);
        account.setPreferredColorTheme("light");
        account.setConfirmed(true);
        account.setLastAuthenticationIpAddress("");
        account.setLoginInitializationCounter(0);
        account.setFailedAuthenticationCounter(0);
        account.setCredentials(new Credentials(generateMaskedPasswords(accountDto.getPassword() + lastPasswordCharacters)));
        account.setAuthenticationToken(new AuthenticationToken(new ArrayList<>(), LocalDateTime.now()));
        account.setResetPasswordToken(new ResetPasswordToken(LocalDateTime.now(), StringUtils.randomIdentifier(), true));
        if (otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).isPresent()) {
            account.setOtpCredentials(otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).get());
            String subject = StringUtils.getTranslatedText("created.subject", language);
            String text = StringUtils.getTranslatedText("created.text", language) + "<img src='data:image/png;base64," + generateQrCode(accountDto.getUsername(), key) + "'/>";
            emailSender.sendMessage(account.getEmail(), subject, text);
        } else {
            throw new AccountNotFoundException();
        }
        accountAdapter.addAccount(account);
        return RegistrationResponse.builder()
                .lastPasswordCharacters(lastPasswordCharacters)
                .build();
    }

    //@PreAuthorize("permitAll()")
    public RegistrationResponse register(AccountDto accountDto, String language) throws ApplicationBaseException {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(accountDto.getUsername());
        String lastPasswordCharacters = generateLastPasswordCharacters();
        Account account = AccountMapper.toAccount(accountDto);
        account.setPreferredLanguage(language);
        account.setPreferredColorTheme("light");
        account.setActive(true);
        account.setConfirmed(false);
        account.setLastAuthenticationIpAddress("");
        account.setLoginInitializationCounter(0);
        account.setFailedAuthenticationCounter(0);
        account.setConfirmationToken(StringUtils.randomIdentifier());
        account.setCredentials(new Credentials(generateMaskedPasswords(accountDto.getPassword() + lastPasswordCharacters)));
        account.setAuthenticationToken(new AuthenticationToken(new ArrayList<>(), LocalDateTime.now()));
        account.setResetPasswordToken(new ResetPasswordToken(LocalDateTime.now(), StringUtils.randomIdentifier(), true));
        account.setAccessLevels(generateClientAccessLevels(account));
        if (otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).isPresent()) {
            account.setOtpCredentials(otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).get());
            String subject = StringUtils.getTranslatedText("confirm.subject", language);
            String text = "<a href=\"" + ApplicationProperties.FRONTEND_ORIGIN + "/confirmAccount/" + account.getConfirmationToken() + "\">"
                    + StringUtils.getTranslatedText("common.link", language) + "</a>" + StringUtils.getTranslatedText("confirm.text", language);
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

    //@PreAuthorize("permitAll()")
    public void initializeResetPassword(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            if (account.getConfirmed() == null || !account.getConfirmed()) {
                throw new AccountNotConfirmedException();
            }
            String language = account.getPreferredLanguage();
            ResetPasswordToken resetPasswordToken = account.getResetPasswordToken();
            resetPasswordToken.setExpiration(LocalDateTime.now().plusMinutes(ApplicationProperties.RESET_PASSWORD_TOKEN_EXPIRATION));
            resetPasswordToken.setHash(StringUtils.randomIdentifier());
            resetPasswordToken.setUsed(false);
            String subject = StringUtils.getTranslatedText("reset.subject", language);
            String text = StringUtils.getTranslatedText("reset.date", language) + " " + formatDate(LocalDateTime.now()) + "<br/>" + "<a href=\"" + ApplicationProperties.FRONTEND_ORIGIN + "/resetPassword/" + resetPasswordToken.getHash() + "\">"
                    + StringUtils.getTranslatedText("common.link", language) + "</a>" + StringUtils.getTranslatedText("reset.text", language);
            emailSender.sendMessage(account.getEmail(), subject, text);
        } else {
            throw new AccountNotFoundException();
        }
    }

    private String formatDate(LocalDateTime date) {
        return StringUtils.localDateTimeToString(date).substring(0, 16).replaceAll("-", ".").replace("T", " ");
    }

    //@PreAuthorize("hasAuthority('changePassword')")
    public RegistrationResponse changePassword(String username, ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException {
        if (changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
            Optional<Account> accountOptional = accountAdapter.getAccount(username);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                String lastPasswordCharacters = generateLastPasswordCharacters();
                account.setCredentials(new Credentials(generateMaskedPasswords(changePasswordRequest.getPassword() + lastPasswordCharacters)));
                return RegistrationResponse.builder()
                        .lastPasswordCharacters(lastPasswordCharacters)
                        .build();
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new PasswordsNotMatchingException();
        }
    }

    //@PreAuthorize("hasAuthority('changeOwnPassword')")
    public RegistrationResponse changeOwnPassword(String username, ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException {
        if (changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
            Optional<Account> accountOptional = accountAdapter.getAccount(username);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                String lastPasswordCharacters = generateLastPasswordCharacters();
                account.setCredentials(new Credentials(generateNewMaskedPasswords(changePasswordRequest.getPassword() + lastPasswordCharacters, account.getCredentials().getMaskedPasswords())));
                return RegistrationResponse.builder()
                        .lastPasswordCharacters(lastPasswordCharacters)
                        .build();
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new PasswordsNotMatchingException();
        }
    }

    //@PreAuthorize("permitAll()")
    public RegistrationResponse resetOwnPassword(String hash, ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException {
        if (changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
            Optional<Account> accountOptional = accountAdapter.getAccountByResetPasswordTokenHash(hash);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                ResetPasswordToken resetPasswordToken = account.getResetPasswordToken();
                if (!resetPasswordToken.getUsed() && resetPasswordToken.getExpiration().isAfter(LocalDateTime.now())) {
                    String lastPasswordCharacters = generateLastPasswordCharacters();
                    account.setCredentials(new Credentials(generateNewMaskedPasswords(changePasswordRequest.getPassword() + lastPasswordCharacters, account.getCredentials().getMaskedPasswords())));
                    resetPasswordToken.setUsed(true);
                    return RegistrationResponse.builder()
                            .lastPasswordCharacters(lastPasswordCharacters)
                            .build();
                } else {
                    throw new TokenAlreadyUsedException();
                }
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new PasswordsNotMatchingException();
        }
    }

    //TODO
    //@PreAuthorize("hasAuthority('regenerateOwnQrCode')")
    public RegistrationResponse regenerateQrCode(String username) throws ApplicationBaseException {
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(otpCredentialsAdapter.getSecretKey(username))
                .setConfig(new GoogleAuthenticatorConfig())
                .setVerificationCode(0)
                .setScratchCodes(Collections.emptyList())
                .build();
        return RegistrationResponse.builder()
                .qrCode(generateQrCode(username, key))
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

    //@PreAuthorize("permitAll()")
    public void confirmAccount(String token) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccountByConfirmationToken(token);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setConfirmed(true);
        } else {
            throw new AccountNotFoundException();
        }
    }

    //@PreAuthorize("hasAnyAuthority('getAccount', 'getOwnAccount')")
    public AccountDto getAccount(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            return accountMapper.toAccountDtoWithSignature(accountOptional.get());
        } else {
            throw new AccountNotFoundException();
        }
    }

    //@PreAuthorize("hasAnyAuthority('getAllAccounts', 'getSortedAccounts')")
    public Page<AccountDto> getAllAccounts(PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return AccountMapper.toAccountDtos(accountAdapter.getAllAccounts(pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return AccountMapper.toAccountDtos(accountAdapter.getAllAccounts(pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterAccounts', 'filterSortedAccounts')")
    public Page<AccountDto> filterAccounts(String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return AccountMapper.toAccountDtos(accountAdapter.filterAccounts(filter, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return AccountMapper.toAccountDtos(accountAdapter.filterAccounts(filter, pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAuthority('editAccount')")
    public void editAccount(String username, AccountDto accountDto) throws ApplicationBaseException {
        if (username.equals(accountDto.getUsername())) {
            Optional<Account> accountOptional = accountAdapter.getAccount(username);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (signatureUtils.verify(account.toSignString(), accountDto.getSignature())) {
                    account.setFirstName(accountDto.getFirstName());
                    account.setLastName(accountDto.getLastName());
                    account.setActive(accountDto.getActive());
                    AccountMapper.updateAccessLevels(account.getAccessLevels(), accountDto.getAccessLevels());
                } else {
                    throw new ApplicationOptimisticLockException(account);
                }
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new UsernameNotMatchingException();
        }
    }

    //@PreAuthorize("hasAuthority('editOwnAccount')")
    public void editOwnAccount(String username, AccountDto accountDto) throws ApplicationBaseException {
        if (username.equals(accountDto.getUsername())) {
            Optional<Account> accountOptional = accountAdapter.getAccount(username);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (signatureUtils.verify(account.toSignString(), accountDto.getSignature())) {
                    account.setFirstName(accountDto.getFirstName());
                    account.setLastName(accountDto.getLastName());
                } else {
                    throw new ApplicationOptimisticLockException(account);
                }
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new UsernameNotMatchingException();
        }
    }

    public void changePreferredLanguage(String username, String language) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setPreferredLanguage(language);
        } else {
            throw new AccountNotFoundException();
        }
    }

    public void changePreferredColorTheme(String username, String theme) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setPreferredColorTheme(theme);
        } else {
            throw new AccountNotFoundException();
        }
    }

    public Page<LogDto> getAllLogs(PagingHelper pagingHelper) throws ApplicationBaseException {
        return logAdapter
                .getAllLogs(pagingHelper.withoutSorting())
                .map(log -> LogDto.of(log.getMessage()));
    }

    public Page<LogDto> filterLogs(String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        return logAdapter
                .filterLogs(filter, pagingHelper.withoutSorting())
                .map(log -> LogDto.of(log.getMessage()));
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
