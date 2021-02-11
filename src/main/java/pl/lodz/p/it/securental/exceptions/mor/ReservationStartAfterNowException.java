package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class ReservationStartAfterNowException extends ApplicationBaseException {

    public static final String KEY_RESERVATION_START_AFTER_NOW = "error.reservation.start.after.now";

    public ReservationStartAfterNowException() {
        super(KEY_RESERVATION_START_AFTER_NOW);
    }

    public ReservationStartAfterNowException(Throwable cause) {
        super(KEY_RESERVATION_START_AFTER_NOW, cause);
    }
}
