package pl.lodz.p.it.securental.adapters.mor;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MorConfiguration;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;
import pl.lodz.p.it.securental.entities.mok.Client;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.mor.AccessLevelRepositoryMor;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.persistence.PersistenceException;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction(MorConfiguration.MOR_TRANSACTION_MANAGER)
public class AccessLevelAdapterMor {

    private final AccessLevelRepositoryMor accessLevelRepository;

    public Optional<Client> getClient(String username) throws ApplicationBaseException {
        try {
            Optional<AccessLevel> accessLevelOptional = accessLevelRepository.findByAccountOtpCredentialsUsernameAndName(username, ApplicationProperties.ACCESS_LEVEL_CLIENT);
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
}
