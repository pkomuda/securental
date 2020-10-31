package pl.lodz.p.it.securental.services.accounts;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.accounts.AccountAdapter;
import pl.lodz.p.it.securental.adapters.accounts.TotpCredentialsAdapter;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.dto.mappers.accounts.AccountMapper;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.AuthenticationToken;
import pl.lodz.p.it.securental.entities.accounts.Credentials;
import pl.lodz.p.it.securental.entities.accounts.MaskedPassword;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountAlreadyExistsException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException;

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
    private final TotpCredentialsAdapter totpCredentialsAdapter;
    private final EmailService emailService;

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
            if (totpCredentialsAdapter.getTotpCredentials(accountDto.getUsername()).isPresent()) {
                account.setTotpCredentials(totpCredentialsAdapter.getTotpCredentials(accountDto.getUsername()).get());
                String subject;
                String text;
                if (language.equals("pl")) {
                    subject = "Potwierdź swoje konto";
                    text = "<a href=\"https://securental.herokuapp.com/confirm/" + account.getConfirmationToken() + "\">Kliknij tutaj</a>, aby potwierdzić swoje konto.";
                } else {
                    subject = "Confirm your account";
                    text = "<a href=\"https://securental.herokuapp.com/confirm/" + account.getConfirmationToken() + "\">Click here</a> to confirm your account.";
                }
                emailService.sendMessage(account.getEmail(), subject, text);
            } else {
                throw new AccountNotFoundException();
            }
            accountAdapter.addAccount(account);
        } else {
            throw new AccountAlreadyExistsException();
        }
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(APPLICATION_NAME, accountDto.getUsername(), key);
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
            accountAdapter.addAccount(account);
        }
        return randomCombination;
    }
}
