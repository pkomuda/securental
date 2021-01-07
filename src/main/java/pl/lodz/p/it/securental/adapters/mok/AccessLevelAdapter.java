package pl.lodz.p.it.securental.adapters.mok;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;
import pl.lodz.p.it.securental.entities.mok.Admin;
import pl.lodz.p.it.securental.entities.mok.Client;
import pl.lodz.p.it.securental.entities.mok.Employee;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.mok.AccessLevelRepository;

import javax.persistence.PersistenceException;
import java.util.Optional;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;

@Service
@AllArgsConstructor
@MandatoryTransaction
public class AccessLevelAdapter {
    
    private final AccessLevelRepository accessLevelRepository;
    
    public Optional<Admin> getAdmin(String username) throws ApplicationBaseException {
        try {
            Optional<AccessLevel> accessLevelOptional = accessLevelRepository.findByAccountOtpCredentialsUsernameAndName(username, ACCESS_LEVEL_ADMIN);
            if (accessLevelOptional.isPresent()) {
                AccessLevel accessLevel = accessLevelOptional.get();
                return Optional.of((Admin) accessLevel);
            } else {
                return Optional.empty();
            }
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<Client> getClient(String username) throws ApplicationBaseException {
        try {
            Optional<AccessLevel> accessLevelOptional = accessLevelRepository.findByAccountOtpCredentialsUsernameAndName(username, ACCESS_LEVEL_CLIENT);
            if (accessLevelOptional.isPresent()) {
                AccessLevel accessLevel = accessLevelOptional.get();
                return Optional.of((Client) accessLevel);
            } else {
                return Optional.empty();
            }
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<Employee> getEmployee(String username) throws ApplicationBaseException {
        try {
            Optional<AccessLevel> accessLevelOptional = accessLevelRepository.findByAccountOtpCredentialsUsernameAndName(username, ACCESS_LEVEL_EMPLOYEE);
            if (accessLevelOptional.isPresent()) {
                AccessLevel accessLevel = accessLevelOptional.get();
                return Optional.of((Employee) accessLevel);
            } else {
                return Optional.empty();
            }
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
