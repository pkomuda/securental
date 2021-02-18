package pl.lodz.p.it.securental.controllers.mor;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import pl.lodz.p.it.securental.dto.model.mor.ReservationDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface ReservationController {

    void addReservation(String username, ReservationDto reservationDto) throws ApplicationBaseException;
    ReservationDto getReservation(String number) throws ApplicationBaseException;
    ReservationDto getOwnReservation(String username, String number) throws ApplicationBaseException;
    void editOwnReservation(String username, String number, ReservationDto reservationDto) throws ApplicationBaseException;
    void changeReservationStatus(String number, ReservationDto reservationDto) throws ApplicationBaseException;
    void changeOwnReservationStatus(String username, String number, ReservationDto reservationDto) throws ApplicationBaseException;
    Page<ReservationDto> getAllReservations(int page, int size, String[] statuses) throws ApplicationBaseException;
    Page<ReservationDto> getSortedReservations(int page, int size, String property, String order, String[] statuses) throws ApplicationBaseException;
    Page<ReservationDto> filterReservations(String filter, int page, int size, String[] statuses) throws ApplicationBaseException;
    Page<ReservationDto> filterSortedReservations(String filter, int page, int size, String property, String order, String[] statuses) throws ApplicationBaseException;
    Page<ReservationDto> getOwnReservations(String username, int page, int size, String[] statuses) throws ApplicationBaseException;
    Page<ReservationDto> getOwnSortedReservations(String username, int page, int size, String property, String order, String[] statuses) throws ApplicationBaseException;
    Page<ReservationDto> filterOwnReservations(String username, String filter, int page, int size, String[] statuses) throws ApplicationBaseException;
    Page<ReservationDto> filterOwnSortedReservations(String username, String filter, int page, int size, String property, String order, String[] statuses) throws ApplicationBaseException;
    void receiveOwnReservation(String username, String number, String signature, MultipartFile front, MultipartFile right, MultipartFile back, MultipartFile left) throws ApplicationBaseException;
    void finishReservation(String number, String signature, MultipartFile front, MultipartFile right, MultipartFile back, MultipartFile left) throws ApplicationBaseException;
}
