package pl.lodz.p.it.securental.services.mor;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.mok.AccessLevelAdapter;
import pl.lodz.p.it.securental.adapters.mop.CarAdapter;
import pl.lodz.p.it.securental.adapters.mor.ReservationAdapter;
import pl.lodz.p.it.securental.adapters.mor.StatusAdapter;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.mappers.mor.ReservationMapper;
import pl.lodz.p.it.securental.dto.mor.ReservationDto;
import pl.lodz.p.it.securental.entities.mok.Client;
import pl.lodz.p.it.securental.entities.mop.Car;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mor.Status;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.mop.CarNotFoundException;
import pl.lodz.p.it.securental.exceptions.mor.ReservationNotFoundException;
import pl.lodz.p.it.securental.exceptions.mor.ReservationNumberNotMatchingException;
import pl.lodz.p.it.securental.exceptions.mor.StatusNotFoundException;
import pl.lodz.p.it.securental.utils.PagingHelper;
import pl.lodz.p.it.securental.utils.SignatureUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;
import static pl.lodz.p.it.securental.utils.StringUtils.randomBase64Url;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class ReservationService {

    private final ReservationAdapter reservationAdapter;
    private final StatusAdapter statusAdapter;
    private final AccessLevelAdapter accessLevelAdapter;
    private final CarAdapter carAdapter;
    private final ReservationMapper reservationMapper;
    private final SignatureUtils signatureUtils;

    public void addReservation(ReservationDto reservationDto) throws ApplicationBaseException {
        Reservation reservation = ReservationMapper.toReservation(reservationDto);
        reservation.setNumber(randomBase64Url());
        reservation.setStatus(getStatus(RESERVATION_STATUS_NEW));

        Optional<Client> clientOptional = accessLevelAdapter.getClient(reservationDto.getClientDto().getUsername());
        if (clientOptional.isPresent() && clientOptional.get().isActive()) {
            reservation.setClient(clientOptional.get());
        } else {
            throw new AccountNotFoundException();
        }

        Optional<Car> carOptional = carAdapter.getCar(reservationDto.getCarDto().getNumber());
        if (carOptional.isPresent()) {
            reservation.setCar(carOptional.get());
        } else {
            throw new CarNotFoundException();
        }

        long minutes = MINUTES.between(reservation.getStartDate(), reservation.getEndDate());
        long hours = (long) Math.ceil(minutes/60.0);
        BigDecimal price = BigDecimal.valueOf(hours).multiply(reservation.getCar().getPrice());
        reservation.setPrice(price);
        
        reservationAdapter.addReservation(reservation);
    }

    public ReservationDto getReservation(String number) throws ApplicationBaseException {
        Optional<Reservation> reservationOptional = reservationAdapter.getReservation(number);
        if (reservationOptional.isPresent()) {
            return reservationMapper.toReservationDtoWithSignature(reservationOptional.get());
        } else {
            throw new ReservationNotFoundException();
        }
    }

    public void editReservation(String number, ReservationDto reservationDto) throws ApplicationBaseException {
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getReservation(number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
                    reservation.setStartDate(reservationDto.getStartDate());
                    reservation.setEndDate(reservationDto.getEndDate());
                    reservation.setStatus(getStatus(reservationDto.getStatus()));
                } else {
                    throw new ApplicationOptimisticLockException();
                }
            } else {
                throw new ReservationNotFoundException();
            }
        } else {
            throw new ReservationNumberNotMatchingException();
        }
    }

    public Page<ReservationDto> getAllReservations(PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.getAllReservations(pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.getAllReservations(pagingHelper.withoutSorting()));
        }
    }

    public Page<ReservationDto> filterReservations(String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterReservations(filter, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterReservations(filter, pagingHelper.withoutSorting()));
        }
    }

    private Status getStatus(String name) throws ApplicationBaseException {
        Optional<Status> statusOptional = statusAdapter.getStatus(name);
        if (statusOptional.isPresent()) {
            return statusOptional.get();
        } else {
            throw new StatusNotFoundException();
        }
    }
}
