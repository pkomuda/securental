package pl.lodz.p.it.securental.services.accounts;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.database.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import javax.persistence.PersistenceException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.MANDATORY)
public class AccountService {

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
