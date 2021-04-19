package pl.lodz.p.it.securental.repositories.mos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MosConfiguration;
import pl.lodz.p.it.securental.entities.mos.Car;
import pl.lodz.p.it.securental.entities.mos.Category;

import java.util.List;
import java.util.Optional;

@Repository
@MandatoryTransaction(MosConfiguration.MOS_TRANSACTION_MANAGER)
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByNumber(String number);

    Page<Car> findAllByCategoryIn(List<Category> categories, Pageable pageable);

    Page<Car> findAllByMakeContainsIgnoreCaseOrModelContainsIgnoreCaseAndCategoryIn(String make, String model, List<Category> categories, Pageable pageable);
}
