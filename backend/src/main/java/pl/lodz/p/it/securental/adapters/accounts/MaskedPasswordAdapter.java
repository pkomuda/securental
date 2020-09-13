package pl.lodz.p.it.securental.adapters.accounts;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.accounts.MaskedPassword;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.database.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.accounts.MaskedPasswordRepository;

import javax.persistence.PersistenceException;
import java.util.List;

@Service
@AllArgsConstructor
@MandatoryTransaction
public class MaskedPasswordAdapter {

    private final MaskedPasswordRepository maskedPasswordRepository;

    public void addMaskedPasswords(List<MaskedPassword> maskedPasswords) throws ApplicationBaseException {
        try {
            maskedPasswordRepository.saveAll(maskedPasswords);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
