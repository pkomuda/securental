package pl.lodz.p.it.securental.adapters.mor;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mor.Status;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.mor.StatusRepository;

import javax.persistence.PersistenceException;
import java.util.Optional;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;

@Service
@AllArgsConstructor
@MandatoryTransaction
public class StatusAdapter {

    private final StatusRepository statusRepository;

    public Optional<Status> getStatusNew() throws ApplicationBaseException {
        try {
            return statusRepository.findByName(RESERVATION_STATUS_NEW);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<Status> getStatusCancelled() throws ApplicationBaseException {
        try {
            return statusRepository.findByName(RESERVATION_STATUS_CANCELLED);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<Status> getStatusFinished() throws ApplicationBaseException {
        try {
            return statusRepository.findByName(RESERVATION_STATUS_FINISHED);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
