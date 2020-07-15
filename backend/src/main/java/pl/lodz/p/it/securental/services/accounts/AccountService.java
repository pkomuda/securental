package pl.lodz.p.it.securental.services.accounts;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountAlreadyExistsException;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void addAccount(Account account) throws ApplicationBaseException {
        if (accountRepository.findByUsername(account.getUsername()).isEmpty()) {
            accountRepository.saveAndFlush(account);
        } else {
            throw new AccountAlreadyExistsException();
        }
    }
}
