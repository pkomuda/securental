package pl.lodz.p.it.securental.repositories.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.entities.accounts.TotpCredentials;

import java.util.Optional;

@Repository
public interface TotpCredentialsRepository extends JpaRepository<TotpCredentials, Long> {

    Optional<TotpCredentials> findByUsername(String username);
}
