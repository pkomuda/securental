package pl.lodz.p.it.securental.dto.mappers.mor;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.adapters.mok.ClientMapper;
import pl.lodz.p.it.securental.dto.mappers.mop.CarMapper;
import pl.lodz.p.it.securental.dto.mor.ReservationDto;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.utils.SignatureUtils;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class ReservationMapper {

    private final SignatureUtils signatureUtils;

    public static Reservation toReservation(ReservationDto reservationDto) {
        return Reservation.builder()
                .startDate(LocalDateTime.parse(reservationDto.getStartDate()))
                .endDate(LocalDateTime.parse(reservationDto.getEndDate()))
                .price(StringUtils.stringToBigDecimal(reservationDto.getPrice()))
                .build();
    }

    public ReservationDto toReservationDtoWithSignature(Reservation reservation) throws ApplicationBaseException {
        return ReservationDto.builder()
                .number(reservation.getNumber())
                .startDate(StringUtils.localDateTimeToString(reservation.getStartDate()))
                .endDate(StringUtils.localDateTimeToString(reservation.getEndDate()))
                .price(StringUtils.bigDecimalToString(reservation.getPrice()))
                .status(reservation.getStatus().getName())
                .clientDto(ClientMapper.toClientDto(reservation.getClient()))
                .carDto(CarMapper.toCarDtoWithoutSignature(reservation.getCar()))
                .signature(signatureUtils.sign(reservation.toSignString()))
                .build();
    }

    public static ReservationDto toReservationDtoWithoutSignature(Reservation reservation) {
        return ReservationDto.builder()
                .number(reservation.getNumber())
                .startDate(StringUtils.localDateTimeToString(reservation.getStartDate()))
                .endDate(StringUtils.localDateTimeToString(reservation.getEndDate()))
                .price(StringUtils.bigDecimalToString(reservation.getPrice()))
                .status(reservation.getStatus().getName())
                .clientDto(ClientMapper.toClientDto(reservation.getClient()))
                .carDto(CarMapper.toCarDtoWithoutSignature(reservation.getCar()))
                .build();
    }

    public static ReservationDto toReservationDtoWithoutCar(Reservation reservation) {
        return ReservationDto.builder()
                .number(reservation.getNumber())
                .startDate(StringUtils.localDateTimeToString(reservation.getStartDate()))
                .endDate(StringUtils.localDateTimeToString(reservation.getEndDate()))
                .price(StringUtils.bigDecimalToString(reservation.getPrice()))
                .status(reservation.getStatus().getName())
                .clientDto(ClientMapper.toClientDto(reservation.getClient()))
                .build();
    }

    public static Page<ReservationDto> toReservationDtos(Page<Reservation> reservations) {
        return reservations.map(ReservationMapper::toReservationDtoWithoutSignature);
    }
}
