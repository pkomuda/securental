package pl.lodz.p.it.securental.controllers.mor.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.mor.ReservationController;
import pl.lodz.p.it.securental.dto.mor.ReservationDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mor.ReservationService;
import pl.lodz.p.it.securental.utils.PagingHelper;

@RestController
@AllArgsConstructor
@NeverTransaction
public class ReservationControllerImpl implements ReservationController {

    private final ReservationService reservationService;

    @Override
    @PostMapping("/reservation")
    public void addReservation(@RequestBody ReservationDto reservationDto) throws ApplicationBaseException {
        reservationService.addReservation(reservationDto);
    }

    @Override
    @GetMapping("/reservation/{number}")
    public ReservationDto getReservation(@PathVariable String number) throws ApplicationBaseException {
        return reservationService.getReservation(number);
    }

    @Override
    @PutMapping("/reservation/{number}")
    public void editReservation(@PathVariable String number,
                                @RequestBody ReservationDto reservationDto) throws ApplicationBaseException {
        reservationService.editReservation(number, reservationDto);
    }

    @Override
    @GetMapping("/reservations/{page}/{size}")
    public Page<ReservationDto> getAllReservations(@PathVariable int page,
                                                   @PathVariable int size) throws ApplicationBaseException {
        return reservationService.getAllReservations(new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/reservations/{page}/{size}/{property}/{order}")
    public Page<ReservationDto> getSortedReservations(@PathVariable int page,
                                                      @PathVariable int size,
                                                      @PathVariable String property,
                                                      @PathVariable String order) throws ApplicationBaseException {
        return reservationService.getAllReservations(new PagingHelper(page, size, property, order));
    }

    @Override
    @GetMapping("/reservations/{filter}/{page}/{size}")
    public Page<ReservationDto> filterReservations(@PathVariable String filter,
                                                   @PathVariable int page,
                                                   @PathVariable int size) throws ApplicationBaseException {
        return reservationService.filterReservations(filter, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/reservations/{filter}/{page}/{size}/{property}/{order}")
    public Page<ReservationDto> filterSortedReservations(@PathVariable String filter,
                                                         @PathVariable int page,
                                                         @PathVariable int size,
                                                         @PathVariable String property,
                                                         @PathVariable String order) throws ApplicationBaseException {
        return reservationService.filterReservations(filter, new PagingHelper(page, size, property, order));
    }
}
