package pl.lodz.p.it.securental.adapters;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.log.Log;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.log.LogRepository;

import javax.persistence.PersistenceException;

@Service
@AllArgsConstructor
@MandatoryTransaction
public class LogAdapter {

    private final LogRepository logRepository;

    public Page<Log> getAllLogs(Pageable pageable) throws ApplicationBaseException {
        try {
            return logRepository.findAll(pageable);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Page<Log> filterLogs(String filter, Pageable pageable) throws ApplicationBaseException {
        try {
            return logRepository.findAllByMessageContainsIgnoreCase(filter, pageable);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
