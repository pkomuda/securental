package pl.lodz.p.it.securental.managers.accounts;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.dto.mappers.accounts.AccountMapper;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.Password;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountAlreadyExistsException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException;
import pl.lodz.p.it.securental.services.accounts.AccountService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static pl.lodz.p.it.securental.utils.StringUtils.integerArrayToString;
import static pl.lodz.p.it.securental.utils.StringUtils.selectCharacters;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.REQUIRES_NEW)
public class AccountManager {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    private List<Password> generatePasswords(Account account, String fullPassword) {
        List<Password> passwords = new ArrayList<>();
        int length = Integer.parseInt(Objects.requireNonNull(env.getProperty("PASSWORD_LENGTH")));
        int characters = Integer.parseInt(Objects.requireNonNull(env.getProperty("PASSWORD_CHARACTERS")));
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(length, characters);
        while (iterator.hasNext()) {
            int[] combination = iterator.next();
            Password password = new Password();
            password.setCombination(integerArrayToString(combination));
            password.setHash(passwordEncoder.encode(selectCharacters(fullPassword.substring(0, length), combination)));
            password.setAccount(account);
            passwords.add(password);
        }
        return passwords;
    }

    public void addAccount(AccountDto accountDto) throws ApplicationBaseException {
        if (accountService.getAccount(accountDto.getUsername()).isEmpty()) {
            Account account = accountMapper.toAccount(accountDto);
            account.setPasswords(generatePasswords(account, accountDto.getPassword()));
            accountService.addAccount(account);
        } else {
            throw new AccountAlreadyExistsException();
        }
    }

    public AccountDto getAccount(String username) throws ApplicationBaseException {
        if (accountService.getAccount(username).isPresent()) {
            return accountMapper.toAccountDto(accountService.getAccount(username).get());
        } else {
            throw new AccountNotFoundException();
        }
    }
}
