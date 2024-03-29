package pl.lodz.p.it.securental.controllers.mor.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lodz.p.it.securental.aop.annotations.NeverTransaction;
import pl.lodz.p.it.securental.aop.annotations.OtpAuthorizationRequired;
import pl.lodz.p.it.securental.controllers.mor.ReservationController;
import pl.lodz.p.it.securental.dto.model.mor.ReservationDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mor.ReservationService;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.PagingHelper;

import java.util.Map;

@RestController
@AllArgsConstructor
@NeverTransaction
public class ReservationControllerImpl implements ReservationController {

    private final ReservationService reservationService;

    @Override
    @OtpAuthorizationRequired
    @PostMapping("/reservation/{username}")
    @PreAuthorize("hasAuthority('addReservation') and #username == authentication.principal.username")
    public void addReservation(@PathVariable String username,
                               @RequestBody ReservationDto reservationDto) throws ApplicationBaseException {
        reservationService.addReservation(username, reservationDto);
    }

    @Override
    @GetMapping("/reservation/{number}")
    @PreAuthorize("hasAuthority('getReservation')")
    public ReservationDto getReservation(@PathVariable String number) throws ApplicationBaseException {
        return reservationService.getReservation(number);
    }

    @Override
    @GetMapping("/reservation/{username}/{number}")
    @PreAuthorize("hasAuthority('getOwnReservation') and #username == authentication.principal.username")
    public ReservationDto getOwnReservation(@PathVariable String username,
                                            @PathVariable String number) throws ApplicationBaseException {
        return reservationService.getOwnReservation(username, number);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/reservation/{username}/{number}")
    @PreAuthorize("hasAuthority('editOwnReservation') and #username == authentication.principal.username")
    public void editOwnReservation(@PathVariable String username,
                                   @PathVariable String number,
                                   @RequestBody ReservationDto reservationDto) throws ApplicationBaseException {
        reservationService.editOwnReservationDates(username, number, reservationDto);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/reservationStatus/{number}")
    @PreAuthorize("hasAuthority('changeReservationStatus')")
    public void changeReservationStatus(@PathVariable String number,
                                        @RequestBody ReservationDto reservationDto) throws ApplicationBaseException {
        reservationService.changeReservationStatus(number, reservationDto);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/reservationStatus/{username}/{number}")
    @PreAuthorize("hasAuthority('changeOwnReservationStatus') and #username == authentication.principal.username")
    public void changeOwnReservationStatus(@PathVariable String username,
                                           @PathVariable String number,
                                           @RequestBody ReservationDto reservationDto) throws ApplicationBaseException {
        reservationService.cancelOwnReservation(username, number, reservationDto);
    }

    @Override
    @GetMapping("/reservations/{page}/{size}")
    @PreAuthorize("hasAuthority('getAllReservations')")
    public Page<ReservationDto> getAllReservations(@PathVariable int page,
                                                   @PathVariable int size,
                                                   @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.getAllReservations(statuses, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/reservations/{page}/{size}/{property}/{order}")
    @PreAuthorize("hasAuthority('getSortedReservations')")
    public Page<ReservationDto> getSortedReservations(@PathVariable int page,
                                                      @PathVariable int size,
                                                      @PathVariable String property,
                                                      @PathVariable String order,
                                                      @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.getAllReservations(statuses, new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @GetMapping("/reservations/{filter}/{page}/{size}")
    @PreAuthorize("hasAuthority('filterReservations')")
    public Page<ReservationDto> filterReservations(@PathVariable String filter,
                                                   @PathVariable int page,
                                                   @PathVariable int size,
                                                   @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.filterReservations(filter, statuses, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/reservations/{filter}/{page}/{size}/{property}/{order}")
    @PreAuthorize("hasAuthority('filterSortedReservations')")
    public Page<ReservationDto> filterSortedReservations(@PathVariable String filter,
                                                         @PathVariable int page,
                                                         @PathVariable int size,
                                                         @PathVariable String property,
                                                         @PathVariable String order,
                                                         @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.filterReservations(filter, statuses, new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @GetMapping("/ownReservations/{username}/{page}/{size}")
    @PreAuthorize("hasAuthority('getOwnReservations') and #username == authentication.principal.username")
    public Page<ReservationDto> getOwnReservations(@PathVariable String username,
                                                   @PathVariable int page,
                                                   @PathVariable int size,
                                                   @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.getOwnReservations(username, statuses, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/ownReservations/{username}/{page}/{size}/{property}/{order}")
    @PreAuthorize("hasAuthority('getOwnSortedReservations') and #username == authentication.principal.username")
    public Page<ReservationDto> getOwnSortedReservations(@PathVariable String username,
                                                         @PathVariable int page,
                                                         @PathVariable int size,
                                                         @PathVariable String property,
                                                         @PathVariable String order,
                                                         @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.getOwnReservations(username, statuses, new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @GetMapping("/ownReservations/{username}/{filter}/{page}/{size}")
    @PreAuthorize("hasAuthority('filterOwnReservations') and #username == authentication.principal.username")
    public Page<ReservationDto> filterOwnReservations(@PathVariable String username,
                                                      @PathVariable String filter,
                                                      @PathVariable int page,
                                                      @PathVariable int size,
                                                      @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.filterOwnReservations(username, filter, statuses, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/ownReservations/{username}/{filter}/{page}/{size}/{property}/{order}")
    @PreAuthorize("hasAuthority('filterOwnSortedReservations') and #username == authentication.principal.username")
    public Page<ReservationDto> filterOwnSortedReservations(@PathVariable String username,
                                                            @PathVariable String filter,
                                                            @PathVariable int page,
                                                            @PathVariable int size,
                                                            @PathVariable String property,
                                                            @PathVariable String order,
                                                            @RequestParam("statuses") String[] statuses) throws ApplicationBaseException {
        return reservationService.filterOwnReservations(username, filter, statuses, new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/receiveOwnReservation/{username}/{number}")
    @PreAuthorize("hasAuthority('receiveOwnReservation') and #username == authentication.principal.username")
    public void receiveOwnReservation(@PathVariable String username,
                                      @PathVariable String number,
                                      @RequestParam("signature") String signature,
                                      @RequestParam(ApplicationProperties.IMAGE_FRONT) MultipartFile front,
                                      @RequestParam(ApplicationProperties.IMAGE_RIGHT) MultipartFile right,
                                      @RequestParam(ApplicationProperties.IMAGE_BACK) MultipartFile back,
                                      @RequestParam(ApplicationProperties.IMAGE_LEFT) MultipartFile left) throws ApplicationBaseException {
        Map<String, MultipartFile> images = Map.of(
                ApplicationProperties.IMAGE_FRONT, front,
                ApplicationProperties.IMAGE_RIGHT, right,
                ApplicationProperties.IMAGE_BACK, back,
                ApplicationProperties.IMAGE_LEFT, left
        );
        reservationService.receiveOwnReservation(username, number, signature, images);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/finishReservation/{number}")
    @PreAuthorize("hasAuthority('finishReservation')")
    public void finishReservation(@PathVariable String number,
                                  @RequestParam("signature") String signature,
                                  @RequestParam(ApplicationProperties.IMAGE_FRONT) MultipartFile front,
                                  @RequestParam(ApplicationProperties.IMAGE_RIGHT) MultipartFile right,
                                  @RequestParam(ApplicationProperties.IMAGE_BACK) MultipartFile back,
                                  @RequestParam(ApplicationProperties.IMAGE_LEFT) MultipartFile left) throws ApplicationBaseException {
        Map<String, MultipartFile> images = Map.of(
                ApplicationProperties.IMAGE_FRONT, front,
                ApplicationProperties.IMAGE_RIGHT, right,
                ApplicationProperties.IMAGE_BACK, back,
                ApplicationProperties.IMAGE_LEFT, left
        );
        reservationService.finishReservation(number, signature, images);
    }

    private String resolvePropertyName(String property) {
        switch (property) {
            case "clientDto.username":
                return "client_account_otpCredentials_username";
            case "carDto.make":
                return "car_make";
            case "carDto.model":
                return "car_model";
            default:
                return property;
        }
    }
}
