package pl.lodz.p.it.securental.adapters.log;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.LogConfiguration;
import pl.lodz.p.it.securental.entities.log.Log;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.log.LogRepository;

import javax.persistence.PersistenceException;

@Service
@AllArgsConstructor
@MandatoryTransaction(LogConfiguration.LOG_TRANSACTION_MANAGER)
public class LogAdapter {

    private final LogRepository logRepository;

    @PreAuthorize("hasAuthority('getAllLogs')")
    public Page<Log> getAllLogs(Pageable pageable) throws ApplicationBaseException {
        try {
            return logRepository.findAll(pageable);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @PreAuthorize("hasAuthority('filterLogs')")
    public Page<Log> filterLogs(String filter, Pageable pageable) throws ApplicationBaseException {
        try {
            return logRepository.findAllByMessageContainsIgnoreCase(filter, pageable);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
