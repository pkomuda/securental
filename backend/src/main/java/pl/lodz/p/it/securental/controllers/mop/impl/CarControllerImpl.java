package pl.lodz.p.it.securental.controllers.mop.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.mop.CarController;
import pl.lodz.p.it.securental.dto.mop.CarDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mop.CarService;
import pl.lodz.p.it.securental.utils.PagingHelper;

@RestController
@AllArgsConstructor
@NeverTransaction
@CrossOrigin(origins = "${FRONTEND_ORIGIN}", allowCredentials = "true")
public class CarControllerImpl implements CarController {

    private final CarService carService;

    @Override
    @PostMapping("/car")
    @PreAuthorize("hasAuthority('addCar')")
    public void addCar(@RequestBody CarDto carDto) throws ApplicationBaseException {
        carService.addCar(carDto);
    }

    @Override
    @GetMapping("/car/{number}")
    @PreAuthorize("permitAll()")
    public CarDto getCar(@PathVariable String number) throws ApplicationBaseException {
        return carService.getCar(number);
    }

    @Override
    @PutMapping("/car/{number}")
    @PreAuthorize("hasAuthority('editCar')")
    public void editCar(@PathVariable String number,
                        @RequestBody CarDto carDto) throws ApplicationBaseException {
        carService.editCar(number, carDto);
    }

    @Override
    @GetMapping("/cars/{page}/{size}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> getAllCars(@PathVariable int page,
                                   @PathVariable int size) throws ApplicationBaseException {
        return carService.getAllCars(new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/cars/{page}/{size}/{property}/{order}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> getSortedCars(@PathVariable int page,
                                      @PathVariable int size,
                                      @PathVariable String property,
                                      @PathVariable String order) throws ApplicationBaseException {
        return carService.getAllCars(new PagingHelper(page, size, property, order));
    }

    @Override
    @GetMapping("/cars/{filter}/{page}/{size}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> filterCars(@PathVariable String filter,
                                   @PathVariable int page,
                                   @PathVariable int size) throws ApplicationBaseException {
        return carService.filterCars(filter, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/cars/{filter}/{page}/{size}/{property}/{order}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> filterSortedCars(@PathVariable String filter,
                                         @PathVariable int page,
                                         @PathVariable int size,
                                         @PathVariable String property,
                                         @PathVariable String order) throws ApplicationBaseException {
        return carService.filterCars(filter, new PagingHelper(page, size, property, order));
    }
}
