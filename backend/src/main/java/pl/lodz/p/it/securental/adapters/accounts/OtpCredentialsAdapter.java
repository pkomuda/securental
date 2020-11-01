package pl.lodz.p.it.securental.adapters.accounts;

import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.accounts.OtpCredentials;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.database.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.accounts.OtpCredentialsRepository;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction
public class OtpCredentialsAdapter implements ICredentialRepository {

    private final OtpCredentialsRepository otpCredentialsRepository;

    @Override
    @SneakyThrows
    public String getSecretKey(String username) {
        Optional<OtpCredentials> otpCredentialsOptional;
        try {
            otpCredentialsOptional = otpCredentialsRepository.findByUsername(username);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
        if (otpCredentialsOptional.isPresent()) {
            return otpCredentialsOptional.get().getSecret();
        } else {
            throw new AccountNotFoundException();
        }
    }

    @Override
    @SneakyThrows
    public void saveUserCredentials(String username, String secret, int validationCode, List<Integer> scratchCodes) {
        try {
            otpCredentialsRepository.saveAndFlush(new OtpCredentials(username, secret, validationCode, scratchCodes));
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<OtpCredentials> getOtpCredentials(String username) throws ApplicationBaseException {
        try {
            return otpCredentialsRepository.findByUsername(username);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
