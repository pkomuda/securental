package pl.lodz.p.it.securental.adapters.mok;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MokConfiguration;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.repositories.mok.AccountRepository;

import javax.persistence.PersistenceException;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction(MokConfiguration.MOK_TRANSACTION_MANAGER)
public class AccountAdapter {

    private final AccountRepository accountRepository;

    @PreAuthorize("permitAll()")
    public void addAccount(Account account) throws ApplicationBaseException {
        try {
            accountRepository.saveAndFlush(account);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @PreAuthorize("permitAll()")
    public Optional<Account> getAccount(String username) throws ApplicationBaseException {
        try {
            return accountRepository.findByOtpCredentialsUsername(username);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @PreAuthorize("permitAll()")
    public Optional<Account> getAccountByConfirmationToken(String token) throws ApplicationBaseException {
        try {
            return accountRepository.findByConfirmationToken(token);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @PreAuthorize("permitAll()")
    public Optional<Account> getAccountByResetPasswordTokenHash(String hash) throws ApplicationBaseException {
        try {
            return accountRepository.findByResetPasswordTokenHash(hash);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('filterAccounts', 'filterSortedAccounts')")
    public Page<Account> filterAccounts(String filter, Pageable pageable) throws ApplicationBaseException {
        try {
            return accountRepository.findAllByOtpCredentialsUsernameContainsIgnoreCaseOrEmailContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(filter,
                    filter,
                    filter,
                    filter,
                    pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('getAllAccounts', 'getSortedAccounts')")
    public Page<Account> getAllAccounts(Pageable pageable) throws ApplicationBaseException {
        try {
            return accountRepository.findAll(pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
