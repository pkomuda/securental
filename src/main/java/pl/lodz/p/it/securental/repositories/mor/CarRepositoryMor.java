package pl.lodz.p.it.securental.repositories.mor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MorConfiguration;
import pl.lodz.p.it.securental.entities.mop.Car;

import java.util.Optional;

@Repository
@MandatoryTransaction(transactionManager = MorConfiguration.MOR_TRANSACTION_MANAGER)
public interface CarRepositoryMor extends JpaRepository<Car, Long> {

    Optional<Car> findByNumber(String number);
}
