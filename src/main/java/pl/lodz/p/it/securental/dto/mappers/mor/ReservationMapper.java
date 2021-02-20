package pl.lodz.p.it.securental.dto.mappers.mor;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.dto.mappers.mok.ClientMapper;
import pl.lodz.p.it.securental.dto.mappers.mop.CarMapper;
import pl.lodz.p.it.securental.dto.model.mor.ReservationDto;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.utils.SignatureUtils;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class ReservationMapper {

    private final CarMapper carMapper;
    private final SignatureUtils signatureUtils;

    public static Reservation toReservation(ReservationDto reservationDto) {
        return Reservation.builder()
                .startDate(parseDate(reservationDto.getStartDate()))
                .endDate(parseDate(reservationDto.getEndDate()))
                .price(StringUtils.stringToBigDecimal(reservationDto.getPrice()))
                .build();
    }

    public ReservationDto toReservationDtoWithSignature(Reservation reservation) throws ApplicationBaseException {
        reservation.getReceivedImageUrls().size();
        reservation.getFinishedImageUrls().size();
        return ReservationDto.builder()
                .number(reservation.getNumber())
                .startDate(StringUtils.localDateTimeToString(reservation.getStartDate()))
                .endDate(StringUtils.localDateTimeToString(reservation.getEndDate()))
                .price(StringUtils.bigDecimalToString(reservation.getPrice()))
                .status(reservation.getStatus().name())
                .clientDto(ClientMapper.toClientDto(reservation.getClient()))
                .carDto(carMapper.toCarDtoWithSignature(reservation.getCar()))
                .receivedImageUrls(reservation.getReceivedImageUrls())
                .finishedImageUrls(reservation.getFinishedImageUrls())
                .signature(signatureUtils.sign(reservation.toSignString()))
                .build();
    }

    public static ReservationDto toReservationDtoWithoutSignature(Reservation reservation) {
        reservation.getReceivedImageUrls().size();
        reservation.getFinishedImageUrls().size();
        return ReservationDto.builder()
                .number(reservation.getNumber())
                .startDate(StringUtils.localDateTimeToString(reservation.getStartDate()))
                .endDate(StringUtils.localDateTimeToString(reservation.getEndDate()))
                .price(StringUtils.bigDecimalToString(reservation.getPrice()))
                .status(reservation.getStatus().name())
                .clientDto(ClientMapper.toClientDto(reservation.getClient()))
                .carDto(CarMapper.toCarDtoWithoutSignature(reservation.getCar()))
                .receivedImageUrls(reservation.getReceivedImageUrls())
                .finishedImageUrls(reservation.getFinishedImageUrls())
                .build();
    }

    public static ReservationDto toReservationDtoWithDatesOnly(Reservation reservation) {
        return ReservationDto.builder()
                .status(reservation.getStatus().name())
                .startDate(StringUtils.localDateTimeToString(reservation.getStartDate()))
                .endDate(StringUtils.localDateTimeToString(reservation.getEndDate()))
                .build();
    }

    public static Page<ReservationDto> toReservationDtos(Page<Reservation> reservations) {
        return reservations.map(ReservationMapper::toReservationDtoWithoutSignature);
    }

    private static LocalDateTime parseDate(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        if (localDateTime.getHour() == 0) {
            return localDateTime.plusDays(1);
        } else {
            return localDateTime;
        }
    }
}
