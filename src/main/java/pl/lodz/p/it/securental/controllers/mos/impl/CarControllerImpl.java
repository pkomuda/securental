package pl.lodz.p.it.securental.controllers.mos.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.aop.annotations.NeverTransaction;
import pl.lodz.p.it.securental.aop.annotations.OtpAuthorizationRequired;
import pl.lodz.p.it.securental.controllers.mos.CarController;
import pl.lodz.p.it.securental.dto.model.mos.CarDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mos.CarService;
import pl.lodz.p.it.securental.utils.PagingHelper;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@NeverTransaction
public class CarControllerImpl implements CarController {

    private final CarService carService;

    @Override
    @PostMapping("/addCar")
    @PreAuthorize("hasAuthority('addCar')")
    public void addCar(@Valid @RequestBody CarDto carDto) throws ApplicationBaseException {
        carService.addCar(carDto);
    }

    @Override
    @GetMapping("/car/{number}")
    @PreAuthorize("permitAll()")
    public CarDto getCar(@PathVariable String number) throws ApplicationBaseException {
        return carService.getCar(number);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/editCar/{number}")
    @PreAuthorize("hasAuthority('editCar')")
    public void editCar(@PathVariable String number,
                        @Valid @RequestBody CarDto carDto) throws ApplicationBaseException {
        carService.editCar(number, carDto);
    }

    @Override
    @GetMapping("/cars/{page}/{size}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> getAllCars(@PathVariable int page,
                                   @PathVariable int size,
                                   @RequestParam("categories") String[] categories) throws ApplicationBaseException {
        return carService.getAllCars(categories, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/cars/{page}/{size}/{property}/{order}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> getSortedCars(@PathVariable int page,
                                      @PathVariable int size,
                                      @PathVariable String property,
                                      @PathVariable String order,
                                      @RequestParam("categories") String[] categories) throws ApplicationBaseException {
        return carService.getAllCars(categories, new PagingHelper(page, size, property, order));
    }

    @Override
    @GetMapping("/cars/{filter}/{page}/{size}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> filterCars(@PathVariable String filter,
                                   @PathVariable int page,
                                   @PathVariable int size,
                                   @RequestParam("categories") String[] categories) throws ApplicationBaseException {
        return carService.filterCars(filter, categories, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/cars/{filter}/{page}/{size}/{property}/{order}")
    @PreAuthorize("permitAll()")
    public Page<CarDto> filterSortedCars(@PathVariable String filter,
                                         @PathVariable int page,
                                         @PathVariable int size,
                                         @PathVariable String property,
                                         @PathVariable String order,
                                         @RequestParam("categories") String[] categories) throws ApplicationBaseException {
        return carService.filterCars(filter, categories, new PagingHelper(page, size, property, order));
    }
}
