package pl.lodz.p.it.securental.repositories.mok;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mok.BlacklistedJwt;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@MandatoryTransaction
public interface BlacklistedJwtRepository extends JpaRepository<BlacklistedJwt, Long> {

    Optional<BlacklistedJwt> findByToken(String token);

    void deleteAllByExpirationBefore(LocalDateTime expiration);
}
