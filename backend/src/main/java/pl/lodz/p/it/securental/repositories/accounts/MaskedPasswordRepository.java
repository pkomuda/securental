package pl.lodz.p.it.securental.repositories.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.accounts.MaskedPassword;

@Repository
@MandatoryTransaction
public interface MaskedPasswordRepository extends JpaRepository<MaskedPassword, Long> {

}
