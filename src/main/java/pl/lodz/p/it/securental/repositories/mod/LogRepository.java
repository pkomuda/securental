package pl.lodz.p.it.securental.repositories.mod;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.configuration.persistence.ModConfiguration;
import pl.lodz.p.it.securental.entities.mod.Log;

@Repository
@MandatoryTransaction(ModConfiguration.MOD_TRANSACTION_MANAGER)
public interface LogRepository extends JpaRepository<Log, Long> {

    @NonNull
    Page<Log> findAll(@NonNull Pageable pageable);

    Page<Log> findAllByMessageContainsIgnoreCase(String message, Pageable pageable);
}
