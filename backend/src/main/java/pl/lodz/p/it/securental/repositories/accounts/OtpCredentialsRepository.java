package pl.lodz.p.it.securental.repositories.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.accounts.OtpCredentials;

import java.util.Optional;

@Repository
@MandatoryTransaction
public interface OtpCredentialsRepository extends JpaRepository<OtpCredentials, Long> {

    Optional<OtpCredentials> findByUsername(String username);
}
