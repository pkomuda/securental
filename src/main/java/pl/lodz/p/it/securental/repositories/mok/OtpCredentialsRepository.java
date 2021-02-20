package pl.lodz.p.it.securental.repositories.mok;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.entities.mok.OtpCredentials;

import java.util.Optional;

@Repository
public interface OtpCredentialsRepository extends JpaRepository<OtpCredentials, Long> {

    Optional<OtpCredentials> findByUsername(String username);
}
