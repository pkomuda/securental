package pl.lodz.p.it.securental.services.mor;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.lodz.p.it.securental.adapters.mok.AccessLevelAdapter;
import pl.lodz.p.it.securental.adapters.mop.CarAdapter;
import pl.lodz.p.it.securental.adapters.mor.ReservationAdapter;
//import pl.lodz.p.it.securental.adapters.mor.StatusAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.mappers.mor.ReservationMapper;
import pl.lodz.p.it.securental.dto.model.mor.ReservationDto;
import pl.lodz.p.it.securental.entities.mok.Client;
import pl.lodz.p.it.securental.entities.mop.Car;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mor.Status;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.UsernameNotMatchingException;
import pl.lodz.p.it.securental.exceptions.mop.CarNotFoundException;
import pl.lodz.p.it.securental.exceptions.mor.*;
import pl.lodz.p.it.securental.utils.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@RequiresNewTransaction
@Retryable(DatabaseConnectionException.class)
public class ReservationService {

    private final ReservationAdapter reservationAdapter;
//    private final StatusAdapter statusAdapter;
    private final AccessLevelAdapter accessLevelAdapter;
    private final CarAdapter carAdapter;
    private final ReservationMapper reservationMapper;
    private final SignatureUtils signatureUtils;
    private final AmazonClient amazonClient;

    //@PreAuthorize("hasAuthority('addReservation')")
    public void addReservation(String username, ReservationDto reservationDto) throws ApplicationBaseException {
        Reservation reservation = ReservationMapper.toReservation(reservationDto);

        validateDates(reservation);

        if (username.equals(reservationDto.getClientDto().getUsername())) {
            reservation.setClient(
                    getClient(username));
        } else {
            throw new UsernameNotMatchingException();
        }

        Car car = getCar(reservationDto.getCarDto().getNumber());
        if (signatureUtils.verify(car.toSignString(), reservationDto.getCarDto().getSignature())) {
            if (car.getActive()) {
                reservation.setCar(car);
            } else {
                throw new CarInactiveException();
            }
        } else {
            throw new ApplicationOptimisticLockException();
        }

        reservation.setStatus(Status.NEW);
        reservation.setPrice(calculateReservationPrice(reservation));
        reservation.setNumber(StringUtils.randomBase64Url());
        car.getReservations().add(reservation);

        reservationAdapter.addReservation(reservation);
    }

    //@PreAuthorize("hasAuthority('getReservation')")
    public ReservationDto getReservation(String number) throws ApplicationBaseException {
        Optional<Reservation> reservationOptional = reservationAdapter.getReservation(number);
        if (reservationOptional.isPresent()) {
            return reservationMapper.toReservationDtoWithSignature(reservationOptional.get());
        } else {
            throw new ReservationNotFoundException();
        }
    }

    //@PreAuthorize("hasAuthority('getOwnReservation')")
    public ReservationDto getOwnReservation(String username, String number) throws ApplicationBaseException {
        Optional<Reservation> reservationOptional = reservationAdapter.getOwnReservation(username, number);
        if (reservationOptional.isPresent()) {
            return reservationMapper.toReservationDtoWithSignature(reservationOptional.get());
        } else {
            throw new ReservationNotFoundException();
        }
    }

    //@PreAuthorize("hasAuthority('editOwnReservation')")
    public void editOwnReservationDates(String username, String number, ReservationDto reservationDto) throws ApplicationBaseException {
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getOwnReservation(username, number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                Car car = getCar(reservationDto.getCarDto().getNumber());
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
                    Reservation temp = ReservationMapper.toReservation(reservationDto);
                    if (signatureUtils.verify(car.toSignString(), reservationDto.getCarDto().getSignature())) {
                        reservation.setStartDate(temp.getStartDate());
                        reservation.setEndDate(temp.getEndDate());
                        reservation.setPrice(calculateReservationPrice(reservation));
                    } else {
                        throw new ApplicationOptimisticLockException();
                    }
                } else {
                    throw new ApplicationOptimisticLockException();
                }
            } else {
                throw new ReservationNotFoundException();
            }
        } else {
            throw new ReservationNumberNotMatchingException();
        }
    }

    public void receiveOwnReservation(String username, String number, String signature, Map<String, MultipartFile> images) throws ApplicationBaseException {
        if (images.size() != 4) {
            throw new ReservationImagesAmountIncorrectException();
        }

        Optional<Reservation> reservationOptional = reservationAdapter.getOwnReservation(username, number);
        if (reservationOptional.isEmpty()) {
            throw new ReservationNotFoundException();
        }

        Reservation reservation = reservationOptional.get();
        if (!signatureUtils.verify(reservation.toSignString(), signature)) {
            throw new ApplicationOptimisticLockException();
        }
        if (!reservation.getStatus().equals(Status.NEW)) {
            throw new IncorrectStatusException();
        }

        //todo
//        if (reservation.getStartDate().isAfter(LocalDateTime.now())) {
//            throw new ReservationStartAfterNowException();
//        }

        List<String> receivedImageUrls = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> entry : images.entrySet()) {
            receivedImageUrls.add(amazonClient.uploadFile(entry.getKey(), entry.getValue()));
        }
        reservation.setStatus(Status.RECEIVED);
        reservation.setReceivedImageUrls(receivedImageUrls);
    }

//    public List<String> s3(MultipartFile[] images) throws ApplicationBaseException {
//        List<String> receivedImageUrls = new ArrayList<>();
//        for (MultipartFile image : images) {
//            receivedImageUrls.add(amazonClient.uploadFile(image));
//        }
//        return receivedImageUrls;
//    }

//    public List<String> s3(MultipartFile images) throws ApplicationBaseException {
//        List<String> receivedImageUrls = new ArrayList<>();
//        receivedImageUrls.add(amazonClient.uploadFile(images));
//        return receivedImageUrls;
//    }

    public void finishOwnReservation(String username, String number, ReservationDto reservationDto, MultipartFile images) throws ApplicationBaseException {

    }

    //@PreAuthorize("hasAuthority('changeReservationStatus')")
    public void changeReservationStatus(String number, ReservationDto reservationDto) throws ApplicationBaseException { // new -> cancelled LUB received -> finished
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getReservation(number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
//                    validateStatuses(reservation.getStatus().name(), reservationDto.getStatus());
//                    reservation.setStatus(getStatus(reservationDto.getStatus()));
                } else {
                    throw new ApplicationOptimisticLockException();
                }
            } else {
                throw new ReservationNotFoundException();
            }
        } else {
            throw new ReservationNumberNotMatchingException();
        }
    }

    //@PreAuthorize("hasAuthority('changeOwnReservationStatus')")
    public void cancelOwnReservation(String username, String number, ReservationDto reservationDto) throws ApplicationBaseException {
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getOwnReservation(username, number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
                    if (reservation.getStatus().name().equals(ApplicationProperties.RESERVATION_STATUS_NEW)
                            && reservationDto.getStatus().equals(ApplicationProperties.RESERVATION_STATUS_CANCELLED)) {
//                        reservation.setStatus(getStatus(reservationDto.getStatus()));
                    } else {
                        throw new IncorrectStatusException();
                    }
                } else {
                    throw new ApplicationOptimisticLockException();
                }
            } else {
                throw new ReservationNotFoundException();
            }
        } else {
            throw new ReservationNumberNotMatchingException();
        }
    }

    //@PreAuthorize("hasAnyAuthority('getAllReservations', 'getSortedReservations')")
    public Page<ReservationDto> getAllReservations(PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.getAllReservations(pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.getAllReservations(pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterReservations', 'filterSortedReservations')")
    public Page<ReservationDto> filterReservations(String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterReservations(filter, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterReservations(filter, pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAnyAuthority('getOwnReservations', 'getOwnSortedReservations')")
    public Page<ReservationDto> getOwnReservations(String username, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.getOwnReservations(username, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.getOwnReservations(username, pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterOwnReservations', 'filterOwnSortedReservations')")
    public Page<ReservationDto> filterOwnReservations(String username, String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterOwnReservations(username, filter, pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterOwnReservations(username, filter, pagingHelper.withoutSorting()));
        }
    }

//    private Status getStatus(String name) throws ApplicationBaseException {
//        Optional<Status> statusOptional = statusAdapter.getStatus(name);
//        if (statusOptional.isPresent()) {
//            return statusOptional.get();
//        } else {
//            throw new StatusNotFoundException();
//        }
//    }

    private Client getClient(String username) throws ApplicationBaseException {
        Optional<Client> clientOptional = accessLevelAdapter.getClient(username);
        if (clientOptional.isPresent() && clientOptional.get().getActive()) {
            return clientOptional.get();
        } else {
            throw new AccountNotFoundException();
        }
    }

    private Car getCar(String number) throws ApplicationBaseException {
        Optional<Car> carOptional = carAdapter.getCar(number);
        if (carOptional.isPresent()) {
            return carOptional.get();
        } else {
            throw new CarNotFoundException();
        }
    }

    private void validateDates(Reservation reservation) throws ApplicationBaseException {
        LocalDateTime now = LocalDateTime.now();
        if (!reservation.getStartDate().isAfter(now)
                || !reservation.getEndDate().isAfter(now)) {
            throw new ReservationDateBeforeNowException();
        } else if (!reservation.getStartDate().isBefore(reservation.getEndDate())) {
            throw new ReservationStartNotBeforeEndException();
        }
    }

//    private List<Status> getAvailableStatuses(Status status) {
//        switch (status) {
//            case NEW:
//                return List.of(Status.CANCELLED, Status.RECEIVED);
//            default:
//                return Collections.emptyList();
//        }
//    }

//    private void validateStatuses(String reservationStatus, String reservationDtoStatus) throws ApplicationBaseException {
//        if (!getAvailableStatuses(reservationStatus).contains(reservationDtoStatus)) {
//            throw new IncorrectStatusException();
//        }
//    }

    private BigDecimal calculateReservationPrice(Reservation reservation) {
        long minutes = ChronoUnit.MINUTES.between(reservation.getStartDate(), reservation.getEndDate());
        long hours = (long) Math.ceil(minutes/60.0);
        return BigDecimal.valueOf(hours).multiply(reservation.getCar().getPrice());
    }
}
