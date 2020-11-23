package pl.lodz.p.it.securental.repositories.mop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mop.Car;

import java.util.Optional;

@Repository
@MandatoryTransaction
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByNumber(String number);
    Page<Car> findAll(Pageable pageable);
    Page<Car> findAllByMakeContainsIgnoreCaseOrModelContainsIgnoreCase(String make, String model, Pageable pageable);
}
