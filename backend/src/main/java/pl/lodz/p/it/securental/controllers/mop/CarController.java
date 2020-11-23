package pl.lodz.p.it.securental.controllers.mop;

import org.springframework.data.domain.Page;
import pl.lodz.p.it.securental.dto.mop.CarDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface CarController {

    void addCar(CarDto carDto) throws ApplicationBaseException;
    CarDto getCar(String number) throws ApplicationBaseException;
    void editCar(String number, CarDto carDto) throws ApplicationBaseException;
    Page<CarDto> getAllCars(int page, int size) throws ApplicationBaseException;
    Page<CarDto> getSortedCars(int page, int size, String property, String order) throws ApplicationBaseException;
}
