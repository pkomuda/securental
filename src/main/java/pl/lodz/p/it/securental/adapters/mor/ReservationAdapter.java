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
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.repositories.mor.ReservationRepository;

import javax.persistence.PersistenceException;
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
    public Page<Reservation> getAllReservations(Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAll(pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterReservations', 'filterSortedReservations')")
    public Page<Reservation> filterReservations(String filter, Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByNumberContainsIgnoreCaseOrClientAccountOtpCredentialsUsernameContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCase(filter,
                    filter,
                    filter,
                    filter,
                    pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('getOwnReservations', 'getOwnSortedReservations')")
    public Page<Reservation> getOwnReservations(String username, Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByClientAccountOtpCredentialsUsername(username, pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterOwnReservations', 'filterOwnSortedReservations')")
    public Page<Reservation> filterOwnReservations(String username, String filter, Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAllByClientAccountOtpCredentialsUsernameAndNumberContainsIgnoreCaseOrCarMakeContainsIgnoreCaseOrCarModelContainsIgnoreCase(username,
                    filter,
                    filter,
                    filter,
                    pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
