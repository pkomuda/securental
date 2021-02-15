package pl.lodz.p.it.securental.controllers.mop;

import org.springframework.data.domain.Page;
import pl.lodz.p.it.securental.dto.model.mop.CarDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface CarController {

    void addCar(CarDto carDto) throws ApplicationBaseException;
    CarDto getCar(String number) throws ApplicationBaseException;
    void editCar(String number, CarDto carDto) throws ApplicationBaseException;
    Page<CarDto> getAllCars(int page, int size, String[] categories) throws ApplicationBaseException;
    Page<CarDto> getSortedCars(int page, int size, String property, String order, String[] categories) throws ApplicationBaseException;
    Page<CarDto> filterCars(String filter, int page, int size, String[] categories) throws ApplicationBaseException;
    Page<CarDto> filterSortedCars(String filter, int page, int size, String property, String order, String[] categories) throws ApplicationBaseException;
}
