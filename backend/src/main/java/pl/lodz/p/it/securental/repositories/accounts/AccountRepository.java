package pl.lodz.p.it.securental.repositories.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.accounts.Account;

import java.util.Optional;

@Repository
@MandatoryTransaction
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByOtpCredentialsUsername(String username);
    Optional<Account> findByConfirmationToken(String token);
}
