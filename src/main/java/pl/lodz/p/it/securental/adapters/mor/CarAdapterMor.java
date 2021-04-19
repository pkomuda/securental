package pl.lodz.p.it.securental.adapters.mor;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MorConfiguration;
import pl.lodz.p.it.securental.entities.mos.Car;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.repositories.mor.CarRepositoryMor;

import javax.persistence.PersistenceException;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction(MorConfiguration.MOR_TRANSACTION_MANAGER)
public class CarAdapterMor {

    private final CarRepositoryMor carRepository;

    public Optional<Car> getCar(String number) throws ApplicationBaseException {
        try {
            return carRepository.findByNumber(number);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
