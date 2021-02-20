package pl.lodz.p.it.securental.repositories.mor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mor.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@MandatoryTransaction
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByNumber(String number);

    Optional<Reservation> findByNumberAndClientAccountOtpCredentialsUsername(String number, String username);

    Page<Reservation> findAllByStatusIn(List<Status> statuses, Pageable pageable);

    Page<Reservation> findAllByNumberContainsIgnoreCaseOrClientAccountOtpCredentialsUsernameContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCaseAndStatusIn(String number, String username, String make, String model, List<Status> statuses, Pageable pageable);

    Page<Reservation> findAllByClientAccountOtpCredentialsUsernameAndStatusIn(String username, List<Status> statuses, Pageable pageable);

    Page<Reservation> findAllByClientAccountOtpCredentialsUsernameAndNumberContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCaseAndStatusIn(String username, String number, String make, String model, List<Status> statuses, Pageable pageable);

    List<Reservation> findAllByStartDateAfterAndStatusIn(LocalDateTime startDate, List<Status> statuses);

    List<Reservation> findAllByStartDateBeforeAndStatusIn(LocalDateTime startDate, List<Status> statuses);
}
