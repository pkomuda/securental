package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class ReservationStartNotBeforeEndException extends ApplicationBaseException {

    public static final String KEY_RESERVATION_START_NOT_BEFORE_END = "error.reservation.start.not.before.end";

    public ReservationStartNotBeforeEndException() {
        super(KEY_RESERVATION_START_NOT_BEFORE_END);
    }

    public ReservationStartNotBeforeEndException(Throwable cause) {
        super(KEY_RESERVATION_START_NOT_BEFORE_END, cause);
    }
}
