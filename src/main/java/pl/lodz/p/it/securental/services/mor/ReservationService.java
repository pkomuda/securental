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
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.mop.CarNotFoundException;
import pl.lodz.p.it.securental.exceptions.mor.ReservationNotFoundException;
import pl.lodz.p.it.securental.exceptions.mor.StatusNotFoundException;
import pl.lodz.p.it.securental.utils.PagingHelper;
import pl.lodz.p.it.securental.utils.SignatureUtils;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
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

        Optional<Status> statusOptional = statusAdapter.getStatusNew();
        if (statusOptional.isPresent()) {
            reservation.setStatus(statusOptional.get());
        } else {
            throw new StatusNotFoundException();
        }

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
        long days = (long) Math.ceil(minutes/1440.0);
        BigDecimal price = BigDecimal.valueOf(reservation.getCar().getPrice()).multiply(BigDecimal.valueOf(days));
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

    public void editReservation(String number, ReservationDto reservationDto) {

    }

    public Page<ReservationDto> getAllReservations(PagingHelper pagingHelper) {
        return null;
    }
}
