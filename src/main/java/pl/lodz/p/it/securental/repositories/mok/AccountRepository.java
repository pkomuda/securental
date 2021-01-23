package pl.lodz.p.it.securental.repositories.mok;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mok.Account;

import java.util.Optional;

@Repository
@MandatoryTransaction
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByOtpCredentialsUsername(String username);
    Optional<Account> findByConfirmationToken(String token);
    @NonNull Page<Account> findAll(@NonNull Pageable pageable);
    Page<Account> findAllByOtpCredentialsUsernameContainsIgnoreCaseOrEmailContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String username,
                                                                                                                                                      String email,
                                                                                                                                                      String firstName,
                                                                                                                                                      String lastName,
                                                                                                                                                      Pageable pageable);
}
