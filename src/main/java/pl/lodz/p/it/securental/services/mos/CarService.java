package pl.lodz.p.it.securental.services.mos;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.mos.CarAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MosConfiguration;
import pl.lodz.p.it.securental.dto.mappers.mos.CarMapper;
import pl.lodz.p.it.securental.dto.model.mos.CarDto;
import pl.lodz.p.it.securental.entities.mos.Car;
import pl.lodz.p.it.securental.entities.mos.Category;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.exceptions.mos.CarNotFoundException;
import pl.lodz.p.it.securental.exceptions.mos.CarNumberNotMatchingException;
import pl.lodz.p.it.securental.utils.PagingHelper;
import pl.lodz.p.it.securental.utils.SignatureUtils;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Retryable(DatabaseConnectionException.class)
@RequiresNewTransaction(MosConfiguration.MOS_TRANSACTION_MANAGER)
public class CarService {

    private final CarAdapter carAdapter;
    private final CarMapper carMapper;
    private final SignatureUtils signatureUtils;

    @PreAuthorize("hasAuthority('addCar')")
    public void addCar(CarDto carDto) throws ApplicationBaseException {
        Car car = CarMapper.toCar(carDto);
        car.setNumber(StringUtils.randomIdentifier());
        carAdapter.addCar(car);
    }

    @PreAuthorize("permitAll()")
    public CarDto getCar(String number) throws ApplicationBaseException {
        Optional<Car> carOptional = carAdapter.getCar(number);
        if (carOptional.isPresent()) {
            return carMapper.toCarDtoWithSignature(carOptional.get());
        } else {
            throw new CarNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('editCar')")
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
                    car.setPrice(StringUtils.stringToBigDecimal(carDto.getPrice()));
                    car.setActive(carDto.getActive());
                } else {
                    throw new ApplicationOptimisticLockException(car);
                }
            } else {
                throw new CarNotFoundException();
            }
        } else {
            throw new CarNumberNotMatchingException();
        }
    }

    @PreAuthorize("permitAll()")
    public Page<CarDto> getAllCars(String[] categories, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return CarMapper.toCarDtos(carAdapter.getAllCars(toCategories(categories), pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return CarMapper.toCarDtos(carAdapter.getAllCars(toCategories(categories), pagingHelper.withoutSorting()));
        }
    }

    @PreAuthorize("permitAll()")
    public Page<CarDto> filterCars(String filter, String[] categories, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return CarMapper.toCarDtos(carAdapter.filterCars(filter, toCategories(categories), pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return CarMapper.toCarDtos(carAdapter.filterCars(filter, toCategories(categories), pagingHelper.withoutSorting()));
        }
    }

    private List<Category> toCategories(String[] categories) {
        return Arrays.stream(categories)
                .map(Category::valueOf)
                .collect(Collectors.toList());
    }
}
