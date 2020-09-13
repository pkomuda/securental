package pl.lodz.p.it.securental.services.accounts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.accounts.AccountAdapter;
import pl.lodz.p.it.securental.adapters.accounts.MaskedPasswordAdapter;
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
import static pl.lodz.p.it.securental.utils.StringUtils.*;

@Slf4j
@Service
@AllArgsConstructor
@RequiresNewTransaction
public class AccountService {

    private final AccountAdapter accountAdapter;
    private final MaskedPasswordAdapter maskedPasswordAdapter;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    private List<MaskedPassword> generateMaskedPasswords(Account account, String fullPassword) {
        List<MaskedPassword> maskedPasswords = new ArrayList<>();
        int fullPasswordLength = getInteger(env, "FULL_PASSWORD_LENGTH");
        List<Integer> maskedPasswordLengthRange = IntStream.rangeClosed(
                getInteger(env, "MASKED_PASSWORD_MIN_LENGTH"),
                getInteger(env, "MASKED_PASSWORD_MAX_LENGTH"))
                .boxed()
                .collect(Collectors.toList());

        for (int maskedPasswordLength : maskedPasswordLengthRange) {
            Iterator<int[]> iterator = combinationsIterator(fullPasswordLength, maskedPasswordLength);
            while (iterator.hasNext()) {
                int[] combination = iterator.next();
                MaskedPassword maskedPassword = new MaskedPassword();
                maskedPassword.setCombination(integerArrayToString(combination));
                maskedPassword.setHash(passwordEncoder.encode(selectCharacters(fullPassword.substring(0, fullPasswordLength), combination)));
                maskedPassword.setAccount(account);
                maskedPasswords.add(maskedPassword);
            }
        }

        return maskedPasswords;
    }

    public void addAccount(AccountDto accountDto) throws ApplicationBaseException {
        if (accountAdapter.getAccount(accountDto.getUsername()).isEmpty()) {
            Account account = accountMapper.toAccount(accountDto);
            List<MaskedPassword> maskedPasswords = generateMaskedPasswords(account, accountDto.getPassword());
            account.setMaskedPasswords(maskedPasswords);
            accountAdapter.addAccount(account);
            maskedPasswordAdapter.addMaskedPasswords(maskedPasswords);
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
