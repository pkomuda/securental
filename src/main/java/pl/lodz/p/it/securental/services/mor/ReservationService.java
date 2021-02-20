package pl.lodz.p.it.securental.services.mor;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.lodz.p.it.securental.adapters.mok.AccessLevelAdapter;
import pl.lodz.p.it.securental.adapters.mop.CarAdapter;
import pl.lodz.p.it.securental.adapters.mor.ReservationAdapter;
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
import pl.lodz.p.it.securental.utils.AmazonClient;
import pl.lodz.p.it.securental.utils.PagingHelper;
import pl.lodz.p.it.securental.utils.SignatureUtils;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
        validateDateOverlap(reservation, reservationAdapter.getAllFutureActiveReservations());

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
            throw new ApplicationOptimisticLockException(car);
        }

        reservation.setStatus(Status.NEW);
//        reservation.setPrice(calculateReservationPrice(reservation));
        reservation.setNumber(StringUtils.randomIdentifier());
        car.getReservations().add(reservation);

        reservationAdapter.addReservation(reservation);
    }

    private void validateDateOverlap(Reservation reservation, List<Reservation> reservations) throws ApplicationBaseException {
        for (Reservation r : reservations) {
            if (!reservation.getStartDate().isAfter(r.getEndDate())
                    && !r.getStartDate().isAfter(reservation.getEndDate())) {
                throw new DateOverlapException();
            }
        }
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
                        reservation.setPrice(StringUtils.stringToBigDecimal(reservationDto.getPrice()));
//                        reservation.setPrice(calculateReservationPrice(reservation));
                    } else {
                        throw new ApplicationOptimisticLockException(car);
                    }
                } else {
                    throw new ApplicationOptimisticLockException(reservation);
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
            throw new ApplicationOptimisticLockException(reservation);
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

    public void finishReservation(String number, String signature, Map<String, MultipartFile> images) throws ApplicationBaseException {
        if (images.size() != 4) {
            throw new ReservationImagesAmountIncorrectException();
        }

        Optional<Reservation> reservationOptional = reservationAdapter.getReservation(number);
        if (reservationOptional.isEmpty()) {
            throw new ReservationNotFoundException();
        }

        Reservation reservation = reservationOptional.get();
        if (!signatureUtils.verify(reservation.toSignString(), signature)) {
            throw new ApplicationOptimisticLockException(reservation);
        }
        if (!reservation.getStatus().equals(Status.RECEIVED)) {
            throw new IncorrectStatusException();
        }

        //todo
//        if (reservation.getEndDate().isBefore(LocalDateTime.now())) {
//            throw new ReservationEndBeforeNowException();
//        }

        List<String> finishedImageUrls = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> entry : images.entrySet()) {
            finishedImageUrls.add(amazonClient.uploadFile(entry.getKey(), entry.getValue()));
        }
        reservation.setStatus(Status.FINISHED);
        reservation.setFinishedImageUrls(finishedImageUrls);
    }

    //@PreAuthorize("hasAuthority('changeReservationStatus')")
    public void changeReservationStatus(String number, ReservationDto reservationDto) throws ApplicationBaseException { // new -> cancelled LUB received -> finished
        if (number.equals(reservationDto.getNumber())) {
            Optional<Reservation> reservationOptional = reservationAdapter.getReservation(number);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                if (signatureUtils.verify(reservation.toSignString(), reservationDto.getSignature())) {
                    //todo
//                    validateStatuses(reservation.getStatus().name(), reservationDto.getStatus());
                    reservation.setStatus(Status.valueOf(reservationDto.getStatus()));
                } else {
                    throw new ApplicationOptimisticLockException(reservation);
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
                    if (reservation.getStatus().equals(Status.NEW)
                            && reservationDto.getStatus().equals(Status.CANCELLED.name())) {
                        reservation.setStatus(Status.CANCELLED);
                    } else {
                        throw new IncorrectStatusException();
                    }
                } else {
                    throw new ApplicationOptimisticLockException(reservation);
                }
            } else {
                throw new ReservationNotFoundException();
            }
        } else {
            throw new ReservationNumberNotMatchingException();
        }
    }

    //@PreAuthorize("hasAnyAuthority('getAllReservations', 'getSortedReservations')")
    public Page<ReservationDto> getAllReservations(String[] statuses, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.getAllReservations(toStatuses(statuses), pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.getAllReservations(toStatuses(statuses), pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterReservations', 'filterSortedReservations')")
    public Page<ReservationDto> filterReservations(String filter, String[] statuses, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterReservations(filter, toStatuses(statuses), pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterReservations(filter, toStatuses(statuses), pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAnyAuthority('getOwnReservations', 'getOwnSortedReservations')")
    public Page<ReservationDto> getOwnReservations(String username, String[] statuses, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.getOwnReservations(username, toStatuses(statuses), pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.getOwnReservations(username, toStatuses(statuses), pagingHelper.withoutSorting()));
        }
    }

    //@PreAuthorize("hasAnyAuthority('filterOwnReservations', 'filterOwnSortedReservations')")
    public Page<ReservationDto> filterOwnReservations(String username, String filter, String[] statuses, PagingHelper pagingHelper) throws ApplicationBaseException {
        try {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterOwnReservations(username, filter, toStatuses(statuses), pagingHelper.withSorting()));
        } catch (PropertyNotFoundException e) {
            return ReservationMapper.toReservationDtos(reservationAdapter.filterOwnReservations(username, filter, toStatuses(statuses), pagingHelper.withoutSorting()));
        }
    }

    private List<Status> toStatuses(String[] statuses) {
        return Arrays.stream(statuses)
                .map(Status::valueOf)
                .collect(Collectors.toList());
    }

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

    private BigDecimal calculateReservationPrice(Reservation reservation) {
        long minutes = ChronoUnit.MINUTES.between(reservation.getStartDate(), reservation.getEndDate());
        long hours = (long) Math.ceil(minutes/60.0);
        return BigDecimal.valueOf(hours).multiply(reservation.getCar().getPrice());
    }
}
