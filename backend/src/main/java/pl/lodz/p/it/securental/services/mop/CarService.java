package pl.lodz.p.it.securental.services.mop;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.mop.CarAdapter;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.mappers.mop.CarMapper;
import pl.lodz.p.it.securental.dto.mop.CarDto;
import pl.lodz.p.it.securental.entities.mop.Car;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.exceptions.mop.CarNotFoundException;
import pl.lodz.p.it.securental.exceptions.mop.NumberNotMatchingException;
import pl.lodz.p.it.securental.utils.PagingHelper;
import pl.lodz.p.it.securental.utils.SignatureUtils;

import java.util.Optional;

import static pl.lodz.p.it.securental.utils.StringUtils.randomBase64;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class CarService {

    private final CarAdapter carAdapter;
    private final CarMapper carMapper;
    private final SignatureUtils signatureUtils;

    public void addCar(CarDto carDto) throws ApplicationBaseException {
        Car car = CarMapper.toCar(carDto);
        car.setNumber(randomBase64());
        carAdapter.addCar(car);
    }

    public CarDto getCar(String number) throws ApplicationBaseException {
        Optional<Car> carOptional = carAdapter.getCar(number);
        if (carOptional.isPresent()) {
            return carMapper.toCarDtoWithSignature(carOptional.get());
        } else {
            throw new CarNotFoundException();
        }
    }

    public void editCar(String number, CarDto carDto) throws ApplicationBaseException {
        if (number.equals(carDto.getNumber())) {
            Optional<Car> carOptional = carAdapter.getCar(number);
            if (carOptional.isPresent()) {
                Car car = carOptional.get();
                if (signatureUtils.verify(car.toSignString(), carDto.getSignature())) {
                    car.setMake(carDto.getMake());
                    car.setModel(carDto.getModel());
                    car.setDescription(carDto.getDescription());
                    car.setProductionYear(carDto.getProductionYear());
                    car.setPrice(carDto.getPrice());
                    car.setActive(carDto.isActive());
                } else {
                    throw new ApplicationOptimisticLockException();
                }
            } else {
                throw new CarNotFoundException();
            }
        } else {
            throw new NumberNotMatchingException();
        }
    }

    public Page<CarDto> getAllCars(PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return CarMapper.toCarDtos(carAdapter.getAllCars(pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return CarMapper.toCarDtos(carAdapter.getAllCars(pagingHelper.withoutSorting()));
        }
    }
}
