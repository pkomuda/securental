package pl.lodz.p.it.securental.services.mor;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.mok.AccessLevelAdapter;
import pl.lodz.p.it.securental.adapters.mop.CarAdapter;
import pl.lodz.p.it.securental.adapters.mor.ReservationAdapter;
import pl.lodz.p.it.securental.adapters.mor.StatusAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
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
import pl.lodz.p.it.securental.exceptions.mok.UsernameNotMatchingException;
import pl.lodz.p.it.securental.exceptions.mop.CarNotFoundException;
import pl.lodz.p.it.securental.exceptions.mor.IncorrectStatusException;
import pl.lodz.p.it.securental.exceptions.mor.ReservationNotFoundException;
import pl.lodz.p.it.securental.exceptions.mor.ReservationNumberNotMatchingException;
import pl.lodz.p.it.securental.exceptions.mor.StatusNotFoundException;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.PagingHelper;
import pl.lodz.p.it.securental.utils.SignatureUtils;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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

    public void addReservation(String username, ReservationDto reservationDto) throws ApplicationBaseException {
        Reservation reservation = ReservationMapper.toReservation(reservationDto);

        if (username.equals(reservationDto.getClientDto().getUsername())) {
            reservation.setClient(getClient(username));
        } else {
            throw new UsernameNotMatchingException();
        }

        Car car = getCar(reservationDto.getCarDto().getNumber());
        reservation.setCar(car);
        reservation.setStatus(getStatus(ApplicationProperties.RESERVATION_STATUS_NEW));
        reservation.setPrice(calculateReservationPrice(reservation));
        reservation.setNumber(StringUtils.randomBase64Url());
        car.getReservations().add(reservation);

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

    public ReservationDto getOwnReservation(String username, String number) throws ApplicationBaseException {
        Optional<Reservation> reservationOptional = reservationAdapter.getOwnReservation(username, number);
        if (reservationOptional.isPresent()) {
            return reservationMapper.toReservationDtoWithSignature(reservationOptional.get());
        } else {
            throw new ReservationNotFoundException();
        }
    }

    public void editOwnReservation(String username, String number, ReservationDto reservationDto) throws ApplicationBaseException {
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getOwnReservation(username, number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
                    Reservation temp = ReservationMapper.toReservation(reservationDto);
                    reservation.setStartDate(temp.getStartDate());
                    reservation.setEndDate(temp.getEndDate());
                    reservation.setPrice(calculateReservationPrice(reservation));
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

    public void changeReservationStatus(String number, ReservationDto reservationDto) throws ApplicationBaseException {
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getReservation(number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
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

    public void changeOwnReservationStatus(String username, String number, ReservationDto reservationDto) throws ApplicationBaseException {
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getOwnReservation(username, number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
                    if (reservation.getStatus().getName().equals(ApplicationProperties.RESERVATION_STATUS_NEW)
                            && reservationDto.getStatus().equals(ApplicationProperties.RESERVATION_STATUS_CANCELLED)) {
                        reservation.setStatus(getStatus(reservationDto.getStatus()));
                    } else {
                        throw new IncorrectStatusException();
                    }
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

    public Page<ReservationDto> getOwnReservations(String username, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.getOwnReservations(username, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.getOwnReservations(username, pagingHelper.withoutSorting()));
        }
    }

    public Page<ReservationDto> filterOwnReservations(String username, String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterOwnReservations(username, filter, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterOwnReservations(username, filter, pagingHelper.withoutSorting()));
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

    private Client getClient(String username) throws ApplicationBaseException {
        Optional<Client> clientOptional = accessLevelAdapter.getClient(username);
        if (clientOptional.isPresent() && clientOptional.get().isActive()) {
            return clientOptional.get();
        } else {
            throw new AccountNotFoundException();
        }
    }

    private Car getCar(String number) throws ApplicationBaseException {
        Optional<Car> carOptional = carAdapter.getCar(number);
        if (carOptional.isPresent()) {
            return carOptional.get();
        } else {
            throw new CarNotFoundException();
        }
    }

    private BigDecimal calculateReservationPrice(Reservation reservation) {
        long minutes = ChronoUnit.MINUTES.between(reservation.getStartDate(), reservation.getEndDate());
        long hours = (long) Math.ceil(minutes/60.0);
        return BigDecimal.valueOf(hours).multiply(reservation.getCar().getPrice());
    }
}
