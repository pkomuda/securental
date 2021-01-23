package pl.lodz.p.it.securental.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.Log;

@Repository
@MandatoryTransaction
public interface LogRepository extends JpaRepository<Log, Long> {

}
