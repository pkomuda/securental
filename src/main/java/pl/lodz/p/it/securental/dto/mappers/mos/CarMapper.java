package pl.lodz.p.it.securental.dto.mappers.mos;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.dto.mappers.mor.ReservationMapper;
import pl.lodz.p.it.securental.dto.model.mor.ReservationDto;
import pl.lodz.p.it.securental.dto.model.mos.CarDto;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mos.Car;
import pl.lodz.p.it.securental.entities.mos.Category;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.SignatureUtils;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CarMapper {

    private final SignatureUtils signatureUtils;

    public static Car toCar(CarDto carDto) {
        return Car.builder()
                .number(carDto.getNumber())
                .make(carDto.getMake())
                .model(carDto.getModel())
                .description(carDto.getDescription())
                .productionYear(carDto.getProductionYear())
                .price(StringUtils.stringToBigDecimal(carDto.getPrice()))
                .active(carDto.getActive())
                .category(Category.valueOf(carDto.getCategory()))
                .reservations(new ArrayList<>())
                .build();
    }

    public CarDto toCarDtoWithSignature(Car car) throws ApplicationBaseException {
        return CarDto.builder()
                .number(car.getNumber())
                .make(car.getMake())
                .model(car.getModel())
                .description(car.getDescription())
                .productionYear(car.getProductionYear())
                .price(StringUtils.bigDecimalToString(car.getPrice()))
                .active(car.getActive())
                .category(car.getCategory().name())
                .reservations(toReservationDtosWithDatesOnly(car.getReservations()))
                .signature(signatureUtils.sign(car.toSignString()))
                .build();
    }

    public static CarDto toCarDtoWithoutSignature(Car car) {
        return CarDto.builder()
                .number(car.getNumber())
                .make(car.getMake())
                .model(car.getModel())
                .description(car.getDescription())
                .productionYear(car.getProductionYear())
                .price(StringUtils.bigDecimalToString(car.getPrice()))
                .active(car.getActive())
                .category(car.getCategory().name())
                .reservations(toReservationDtosWithDatesOnly(car.getReservations()))
                .build();
    }

    public static Page<CarDto> toCarDtos(Page<Car> cars) {
        return cars.map(CarMapper::toCarDtoWithoutSignature);
    }

    private static List<ReservationDto> toReservationDtosWithDatesOnly(List<Reservation> reservations) {
        return reservations.stream()
                .filter(reservation -> ApplicationProperties.ACTIVE_STATUSES.contains(reservation.getStatus()))
                .map(ReservationMapper::toReservationDtoWithDatesOnly)
                .collect(Collectors.toList());
    }
}
