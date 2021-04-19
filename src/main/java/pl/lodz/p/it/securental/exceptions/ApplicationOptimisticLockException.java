package pl.lodz.p.it.securental.exceptions;

import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mos.Car;

public class ApplicationOptimisticLockException extends ApplicationBaseException {

    public static final String KEY_ACCOUNT_OPTIMISTIC_LOCK = "error.account.optimistic.lock";
    public static final String KEY_CAR_OPTIMISTIC_LOCK = "error.car.optimistic.lock";
    public static final String KEY_RESERVATION_OPTIMISTIC_LOCK = "error.reservation.optimistic.lock";

    public ApplicationOptimisticLockException(Account account) {
        super(KEY_ACCOUNT_OPTIMISTIC_LOCK);
    }

    public ApplicationOptimisticLockException(Car car) {
        super(KEY_CAR_OPTIMISTIC_LOCK);
    }

    public ApplicationOptimisticLockException(Reservation reservation) {
        super(KEY_RESERVATION_OPTIMISTIC_LOCK);
    }
}
