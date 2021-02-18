package pl.lodz.p.it.securental.adapters.mor;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.MandatoryTransaction;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mor.Status;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.repositories.mor.ReservationRepository;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@MandatoryTransaction
public class ReservationAdapter {

    private final ReservationRepository reservationRepository;

    //@PreAuthorize("hasAuthority('addReservation')")
    public void addReservation(Reservation reservation) throws ApplicationBaseException {
        try {
            reservationRepository.saveAndFlush(reservation);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('getReservation', 'changeReservationStatus')")
    public Optional<Reservation> getReservation(String number) throws ApplicationBaseException {
        try {
            return reservationRepository.findByNumber(number);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('getOwnReservation', 'editOwnReservation', 'changeOwnReservationStatus')")
    public Optional<Reservation> getOwnReservation(String username, String number) throws ApplicationBaseException {
        try {
            return reservationRepository.findByNumberAndClientAccountOtpCredentialsUsername(number, username);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('getAllReservations', 'getSortedReservations')")
    public Page<Reservation> getAllReservations(List<Status> statuses, Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByStatusIn(statuses, pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterReservations', 'filterSortedReservations')")
    public Page<Reservation> filterReservations(String filter, List<Status> statuses, Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByNumberContainsIgnoreCaseOrClientAccountOtpCredentialsUsernameContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCaseAndStatusIn(filter,
                    filter,
                    filter,
                    filter,
                    statuses,
                    pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('getOwnReservations', 'getOwnSortedReservations')")
    public Page<Reservation> getOwnReservations(String username, List<Status> statuses, Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByClientAccountOtpCredentialsUsernameAndStatusIn(username, statuses, pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterOwnReservations', 'filterOwnSortedReservations')")
    public Page<Reservation> filterOwnReservations(String username, String filter, List<Status> statuses, Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByClientAccountOtpCredentialsUsernameAndNumberContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCaseAndStatusIn(username,
                    filter,
                    filter,
                    filter,
                    statuses,
                    pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public List<Reservation> getAllFutureActiveReservations() throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByStartDateAfterAndStatusIn(LocalDateTime.now(),
                    ApplicationProperties.ACTIVE_STATUSES);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
