package pl.lodz.p.it.securental.repositories.mor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mor.Status;

import java.util.Optional;

@Repository
@MandatoryTransaction
public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findByName(String name);
}
