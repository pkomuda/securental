package pl.lodz.p.it.securental.controllers.mor;

import org.springframework.data.domain.Page;
import pl.lodz.p.it.securental.dto.mor.ReservationDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface ReservationController {

    void addReservation(ReservationDto reservationDto) throws ApplicationBaseException;
    ReservationDto getReservation(String number) throws ApplicationBaseException;
    void editReservation(String number, ReservationDto reservationDto) throws ApplicationBaseException;
    Page<ReservationDto> getAllReservations(int page, int size) throws ApplicationBaseException;
    Page<ReservationDto> getSortedReservations(int page, int size, String property, String order) throws ApplicationBaseException;
    Page<ReservationDto> filterReservations(String filter, int page, int size) throws ApplicationBaseException;
    Page<ReservationDto> filterSortedReservations(String filter, int page, int size, String property, String order) throws ApplicationBaseException;
}
