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
import pl.lodz.p.it.securental.entities.accounts.MaskedPassword;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountAlreadyExistsException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.math3.util.CombinatoricsUtils.combinationsIterator;
import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;
import static pl.lodz.p.it.securental.utils.StringUtils.integerArrayToString;
import static pl.lodz.p.it.securental.utils.StringUtils.selectCharacters;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class AccountService {

    private final AccountAdapter accountAdapter;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthenticator googleAuthenticator;
    private final TotpCredentialsAdapter totpCredentialsAdapter;

    private List<MaskedPassword> generateMaskedPasswords(String fullPassword) {
        List<MaskedPassword> maskedPasswords = new ArrayList<>();
        List<Integer> maskedPasswordLengthRange = IntStream.rangeClosed(
                MASKED_PASSWORD_MIN_LENGTH,
                MASKED_PASSWORD_MAX_LENGTH)
                .boxed()
                .collect(Collectors.toList());

        for (int maskedPasswordLength : maskedPasswordLengthRange) {
            Iterator<int[]> iterator = combinationsIterator(FULL_PASSWORD_LENGTH, maskedPasswordLength);
            while (iterator.hasNext()) {
                int[] combination = iterator.next();
                MaskedPassword maskedPassword = new MaskedPassword();
                maskedPassword.setCombination(passwordEncoder.encode(integerArrayToString(combination)));
                maskedPassword.setHash(passwordEncoder.encode(selectCharacters(fullPassword.substring(0, FULL_PASSWORD_LENGTH), combination)));
                maskedPasswords.add(maskedPassword);
            }
        }
        return maskedPasswords;
    }

    public void addAccount(AccountDto accountDto) throws ApplicationBaseException {
        if (accountAdapter.getAccount(accountDto.getUsername()).isEmpty()) {
            Account account = AccountMapper.toAccount(accountDto);
            account.setMaskedPasswords(generateMaskedPasswords(accountDto.getPassword()));
            accountAdapter.addAccount(account);
        } else {
            throw new AccountAlreadyExistsException();
        }
    }

    public String register(AccountDto accountDto) throws ApplicationBaseException {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(accountDto.getUsername());
        if (accountAdapter.getAccount(accountDto.getUsername()).isEmpty()) {
            Account account = AccountMapper.toAccount(accountDto);
            account.setMaskedPasswords(generateMaskedPasswords(accountDto.getPassword()));
            if (totpCredentialsAdapter.getTotpCredentials(accountDto.getUsername()).isPresent()) {
                account.setTotpCredentials(totpCredentialsAdapter.getTotpCredentials(accountDto.getUsername()).get());
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
        if (accountAdapter.getAccount(username).isPresent()) {
            return AccountMapper.toAccountDto(accountAdapter.getAccount(username).get());
        } else {
            throw new AccountNotFoundException();
        }
    }
}
