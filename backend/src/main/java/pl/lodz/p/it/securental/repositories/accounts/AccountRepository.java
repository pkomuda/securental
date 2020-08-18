package pl.lodz.p.it.securental.repositories.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import java.util.Optional;

@Repository
@Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.MANDATORY)
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
