package pl.lodz.p.it.securental.services.accounts;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.accounts.AccountAdapter;
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
import java.util.Objects;

import static pl.lodz.p.it.securental.utils.StringUtils.integerArrayToString;
import static pl.lodz.p.it.securental.utils.StringUtils.selectCharacters;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class AccountService {

    private final AccountAdapter accountAdapter;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    private List<MaskedPassword> generateMaskedPasswords(Account account, String fullPassword) {
        List<MaskedPassword> maskedPasswords = new ArrayList<>();
        int length = Integer.parseInt(Objects.requireNonNull(env.getProperty("PASSWORD_LENGTH")));
        int characters = Integer.parseInt(Objects.requireNonNull(env.getProperty("PASSWORD_CHARACTERS")));
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(length, characters);
        while (iterator.hasNext()) {
            int[] combination = iterator.next();
            MaskedPassword maskedPassword = new MaskedPassword();
            maskedPassword.setCombination(integerArrayToString(combination));
            maskedPassword.setHash(passwordEncoder.encode(selectCharacters(fullPassword.substring(0, length), combination)));
            maskedPassword.setAccount(account);
            maskedPasswords.add(maskedPassword);
        }
        return maskedPasswords;
    }

    public void addAccount(AccountDto accountDto) throws ApplicationBaseException {
        if (accountAdapter.getAccount(accountDto.getUsername()).isEmpty()) {
            Account account = accountMapper.toAccount(accountDto);
            account.setMaskedPasswords(generateMaskedPasswords(account, accountDto.getPassword()));
            accountAdapter.addAccount(account);
        } else {
            throw new AccountAlreadyExistsException();
        }
    }

    public AccountDto getAccount(String username) throws ApplicationBaseException {
        if (accountAdapter.getAccount(username).isPresent()) {
            return accountMapper.toAccountDto(accountAdapter.getAccount(username).get());
        } else {
            throw new AccountNotFoundException();
        }
    }
}
