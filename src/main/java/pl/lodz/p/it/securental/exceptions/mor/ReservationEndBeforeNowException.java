package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class ReservationEndBeforeNowException extends ApplicationBaseException {

    public static final String KEY_RESERVATION_END_BEFORE_NOW = "error.reservation.end.before.now";

    public ReservationEndBeforeNowException() {
        super(KEY_RESERVATION_END_BEFORE_NOW);
    }

    public ReservationEndBeforeNowException(Throwable cause) {
        super(KEY_RESERVATION_END_BEFORE_NOW, cause);
    }
}
