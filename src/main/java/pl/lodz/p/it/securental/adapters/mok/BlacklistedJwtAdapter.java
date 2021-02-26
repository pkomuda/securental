package pl.lodz.p.it.securental.adapters.mok;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MokConfiguration;
import pl.lodz.p.it.securental.entities.mok.BlacklistedJwt;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.mok.BlacklistedJwtRepository;

import javax.persistence.PersistenceException;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction(transactionManager = MokConfiguration.MOK_TRANSACTION_MANAGER)
public class BlacklistedJwtAdapter {

    private final BlacklistedJwtRepository blacklistedJwtRepository;

    //@PreAuthorize("hasAuthority('logout')")
    public void addBlacklistedJwt(BlacklistedJwt blacklistedJwt) throws ApplicationBaseException {
        try {
            blacklistedJwtRepository.saveAndFlush(blacklistedJwt);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<BlacklistedJwt> getBlacklistedJwt(String token) throws ApplicationBaseException {
        try {
            return blacklistedJwtRepository.findByToken(token);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
