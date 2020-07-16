package pl.lodz.p.it.securental.services.accounts;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.Password;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountAlreadyExistsException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pl.lodz.p.it.securental.utils.CombinatoricsUtils.generateCombinations;
import static pl.lodz.p.it.securental.utils.StringUtils.intArrayToString;
import static pl.lodz.p.it.securental.utils.StringUtils.selectCharacters;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.MANDATORY)
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    private List<Password> generatePasswords(Account account) {
        List<Password> passwords = new ArrayList<>();

        int length = Integer.parseInt(Objects.requireNonNull(env.getProperty("PASSWORD_LENGTH")));
        int chars = Integer.parseInt(Objects.requireNonNull(env.getProperty("PASSWORD_CHARS")));

        for (int[] arr : generateCombinations(length, chars)) {
            Password password = new Password();
            password.setCombination(intArrayToString(arr));
            password.setHash(passwordEncoder.encode(selectCharacters(account.getPassword().substring(0, length), arr)));
            password.setAccount(account);
            passwords.add(password);
        }

        return passwords;
    }

    public void addAccount(Account account) throws ApplicationBaseException {
        if (accountRepository.findByUsername(account.getUsername()).isEmpty()) {
            account.setPasswords(generatePasswords(account));
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.saveAndFlush(account);
        } else {
            throw new AccountAlreadyExistsException();
        }
    }

    public Account getAccount(String username) throws ApplicationBaseException {
        if (accountRepository.findByUsername(username).isPresent()) {
            return accountRepository.findByUsername(username).get();
        } else {
            throw new AccountNotFoundException();
        }
    }
}
