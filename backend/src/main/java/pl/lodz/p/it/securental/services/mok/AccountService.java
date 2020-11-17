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
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.mok.AccountAdapter;
import pl.lodz.p.it.securental.adapters.mok.OtpCredentialsAdapter;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.mappers.mok.AccountMapper;
import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.entities.mok.AuthenticationToken;
import pl.lodz.p.it.securental.entities.mok.Credentials;
import pl.lodz.p.it.securental.entities.mok.MaskedPassword;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.mok.AccountAlreadyExistsException;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.QrCodeGenerationException;
import pl.lodz.p.it.securental.utils.EmailSender;
import pl.lodz.p.it.securental.utils.PagingHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.apache.commons.math3.util.CombinatoricsUtils.combinationsIterator;
import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;
import static pl.lodz.p.it.securental.utils.StringUtils.*;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class AccountService {

    private final AccountAdapter accountAdapter;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthenticator googleAuthenticator;
    private final OtpCredentialsAdapter otpCredentialsAdapter;
    private final EmailSender emailSender;

    private List<MaskedPassword> generateMaskedPasswords(String fullPassword) {
        List<MaskedPassword> maskedPasswords = new ArrayList<>();
        int[] maskedPasswordLengthRange = IntStream.rangeClosed(
                MASKED_PASSWORD_MIN_LENGTH,
                MASKED_PASSWORD_MAX_LENGTH)
                .toArray();

        for (int maskedPasswordLength : maskedPasswordLengthRange) {
            Iterator<int[]> iterator = combinationsIterator(FULL_PASSWORD_LENGTH, maskedPasswordLength);
            while (iterator.hasNext()) {
                int[] combination = iterator.next();
                MaskedPassword maskedPassword = new MaskedPassword();
                maskedPassword.setCombination(passwordEncoder.encode(integerArrayToString(combination)));
                maskedPassword.setHash(passwordEncoder.encode(selectCharacters(fullPassword, combination)));
                maskedPasswords.add(maskedPassword);
            }
        }
        return maskedPasswords;
    }

    public void addAccount(AccountDto accountDto) throws ApplicationBaseException {
        if (accountAdapter.getAccount(accountDto.getUsername()).isEmpty()) {
            Account account = AccountMapper.toAccount(accountDto);
            account.setCredentials(new Credentials(generateMaskedPasswords(accountDto.getPassword())));
            account.setAuthenticationToken(new AuthenticationToken(new ArrayList<>(), LocalDateTime.now()));
            accountAdapter.addAccount(account);
        } else {
            throw new AccountAlreadyExistsException();
        }
    }

    public String register(AccountDto accountDto, String language) throws ApplicationBaseException {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(accountDto.getUsername());
        if (accountAdapter.getAccount(accountDto.getUsername()).isEmpty()) {
            Account account = AccountMapper.toAccount(accountDto);
            account.setActive(true);
            account.setConfirmed(false);
            account.setConfirmationToken(randomBase64());
            account.setCredentials(new Credentials(generateMaskedPasswords(accountDto.getPassword())));
            account.setAuthenticationToken(new AuthenticationToken(new ArrayList<>(), LocalDateTime.now()));
            if (otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).isPresent()) {
                account.setOtpCredentials(otpCredentialsAdapter.getOtpCredentials(accountDto.getUsername()).get());
                String subject = getTranslatedText("confirm.subject", language);
                String text = "<a href=\"" + FRONTEND_ORIGIN + "/confirm/" + account.getConfirmationToken() + "\">"
                        + getTranslatedText("confirm.link", language) + "</a>" + getTranslatedText("confirm.text", language);
                emailSender.sendMessage(account.getEmail(), subject, text);
            } else {
                throw new AccountNotFoundException();
            }
            accountAdapter.addAccount(account);
        } else {
            throw new AccountAlreadyExistsException();
        }

        byte[] qrCode;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BitMatrix bitMatrix = qrCodeWriter.encode(GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(APPLICATION_NAME, accountDto.getUsername(), key), BarcodeFormat.QR_CODE, 200, 200);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            qrCode = outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new QrCodeGenerationException(e);
        }
        return base64(qrCode);
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
            return AccountMapper.toAccountDto(accountOptional.get());
        } else {
            throw new AccountNotFoundException();
        }
    }

    public List<Integer> initializeLogin(String username) throws ApplicationBaseException {
        List<Integer> randomCombination = randomCombination(
                FULL_PASSWORD_LENGTH,
                MASKED_PASSWORD_MIN_LENGTH,
                MASKED_PASSWORD_MAX_LENGTH);
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            AuthenticationToken authenticationToken = account.getAuthenticationToken();
            authenticationToken.setCombination(randomCombination);
            authenticationToken.setExpiration(LocalDateTime.now().plusMinutes(AUTHENTICATION_TOKEN_EXPIRATION));
//            accountAdapter.addAccount(account);
        }
        return randomCombination;
    }

    public Page<AccountDto> filterAccounts(String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return toDtos(accountAdapter.filterAccounts(filter, pagingHelper.withSorting()));
        } catch (PropertyReferenceException e) {
            return toDtos(accountAdapter.filterAccounts(filter, pagingHelper.withoutSorting()));
        }
    }

    public Page<AccountDto> getAllAccounts(PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return toDtos(accountAdapter.getAllAccounts(pagingHelper.withSorting()));
        } catch (PropertyReferenceException e) {
            return toDtos(accountAdapter.getAllAccounts(pagingHelper.withoutSorting()));
        }
    }

    public void add(AccountDto accountDto) throws ApplicationBaseException {
        accountAdapter.addAccount(toAccount(accountDto));
    }

    private static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .active(account.isActive())
                .confirmed(account.isConfirmed())
                .build();
    }

    private static Page<AccountDto> toDtos(Page<Account> accounts) {
        return accounts.map(AccountService::toDto);
    }

    private static Account toAccount(AccountDto accountDto) {
        return Account.builder()
                .email(accountDto.getEmail())
                .firstName(accountDto.getFirstName())
                .lastName(accountDto.getLastName())
                .active(accountDto.isActive())
                .confirmed(accountDto.isConfirmed())
                .fullPassword(accountDto.getPassword())
                .build();
    }
}
