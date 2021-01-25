package pl.lodz.p.it.securental.repositories.mok;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;

import java.util.Optional;

@Repository
@MandatoryTransaction
public interface AccessLevelRepository extends JpaRepository<AccessLevel, Long> {

    Optional<AccessLevel> findByAccountOtpCredentialsUsernameAndName(String username, String name);
}
