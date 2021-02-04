package pl.lodz.p.it.securental.repositories.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.log.Log;

@Repository
@MandatoryTransaction
public interface LogRepository extends JpaRepository<Log, Long> {

}
