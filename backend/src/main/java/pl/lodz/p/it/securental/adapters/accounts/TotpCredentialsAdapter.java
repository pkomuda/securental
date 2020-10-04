package pl.lodz.p.it.securental.adapters.accounts;

import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.entities.accounts.TotpCredentials;
import pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.database.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.accounts.TotpCredentialsRepository;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class TotpCredentialsAdapter implements ICredentialRepository {

    private final TotpCredentialsRepository totpCredentialsRepository;

    @Override
    @SneakyThrows
    public String getSecretKey(String username) {
        Optional<TotpCredentials> totpCredentialsOptional;
        try {
            totpCredentialsOptional = totpCredentialsRepository.findByUsername(username);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
        if (totpCredentialsOptional.isPresent()) {
            return totpCredentialsOptional.get().getSecret();
        } else {
            throw new AccountNotFoundException();
        }
    }

    @Override
    @SneakyThrows
    public void saveUserCredentials(String username, String secret, int validationCode, List<Integer> scratchCodes) {
        try {
            totpCredentialsRepository.saveAndFlush(new TotpCredentials(username, secret, validationCode, scratchCodes));
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
