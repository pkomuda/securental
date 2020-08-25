package pl.lodz.p.it.securental.adapters.accounts;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.database.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import javax.persistence.PersistenceException;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction
public class AccountAdapter {

    private final AccountRepository accountRepository;

    public void addAccount(Account account) throws ApplicationBaseException {
        try {
            accountRepository.saveAndFlush(account);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<Account> getAccount(String username) throws ApplicationBaseException {
        try {
            return accountRepository.findByUsername(username);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
