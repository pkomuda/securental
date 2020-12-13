package pl.lodz.p.it.securental.dto.mappers.mop;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.dto.mop.CarDto;
import pl.lodz.p.it.securental.entities.mop.Car;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.utils.SignatureUtils;

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
                .price(carDto.getPrice())
                .active(carDto.isActive())
                .build();
    }

    public CarDto toCarDtoWithSignature(Car car) throws ApplicationBaseException {
        return CarDto.builder()
                .number(car.getNumber())
                .make(car.getMake())
                .model(car.getModel())
                .description(car.getDescription())
                .productionYear(car.getProductionYear())
                .price(car.getPrice())
                .active(car.isActive())
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
                .price(car.getPrice())
                .active(car.isActive())
                .build();
    }

    public static Page<CarDto> toCarDtos(Page<Car> cars) {
        return cars.map(CarMapper::toCarDtoWithoutSignature);
    }
}
