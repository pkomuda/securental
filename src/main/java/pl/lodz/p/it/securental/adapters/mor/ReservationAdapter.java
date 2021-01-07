package pl.lodz.p.it.securental.adapters.mor;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.MandatoryTransaction;
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

    public void addReservation(Reservation reservation) throws ApplicationBaseException {
        try {
            reservationRepository.saveAndFlush(reservation);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Optional<Reservation> getReservation(String number) throws ApplicationBaseException {
        try {
            return reservationRepository.findByNumber(number);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    public Page<Reservation> getAllReservations(Pageable pageable) throws ApplicationBaseException {
        try {
            return reservationRepository.findAll(pageable);
        } catch (PropertyReferenceException e) {
            throw new PropertyNotFoundException(e);
        } catch (PersistenceException | DataAccessException e) {
            throw new DatabaseConnectionException(e);
        }
    }

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
}
