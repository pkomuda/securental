package pl.lodz.p.it.securental.adapters.mop;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MopConfiguration;
import pl.lodz.p.it.securental.entities.mop.Car;
import pl.lodz.p.it.securental.entities.mop.Category;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.repositories.mop.CarRepository;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction(transactionManager = MopConfiguration.MOP_TRANSACTION_MANAGER)
public class CarAdapter {

    private final CarRepository carRepository;

    //@PreAuthorize("hasAuthority('addCar')")
    public void addCar(Car car) throws ApplicationBaseException {
        try {
            carRepository.saveAndFlush(car);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("permitAll()")
    public Optional<Car> getCar(String number) throws ApplicationBaseException {
        try {
            return carRepository.findByNumber(number);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("permitAll()")
    public Page<Car> getAllCars(List<Category> categories, Pageable pageable) throws ApplicationBaseException {
        try {
            return carRepository.findAllByCategoryIn(categories, pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("permitAll()")
    public Page<Car> filterCars(String filter, List<Category> categories, Pageable pageable) throws ApplicationBaseException {
        try {
            return carRepository.findAllByMakeContainsIgnoreCaseOrModelContainsIgnoreCaseAndCategoryIn(filter,
                    filter,
                    categories,
                    pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
