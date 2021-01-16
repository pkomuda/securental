package pl.lodz.p.it.securental.repositories.mor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mor.Reservation;

import java.util.Optional;

@Repository
@MandatoryTransaction
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByNumber(String number);
    Optional<Reservation> findByNumberAndClientAccountOtpCredentialsUsername(String number, String username);
    Page<Reservation> findAll(Pageable pageable);
    Page<Reservation> findAllByNumberContainsIgnoreCaseOrClientAccountOtpCredentialsUsernameContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCase(String number, String username, String make, String model, Pageable pageable);
    Page<Reservation> findAllByClientAccountOtpCredentialsUsername(String username, Pageable pageable);
    Page<Reservation> findAllByClientAccountOtpCredentialsUsernameAndNumberContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCase(String username, String number, String make, String model, Pageable pageable);
}
