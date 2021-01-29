package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class ReservationDateBeforeNowException extends ApplicationBaseException {

    public static final String KEY_RESERVATION_DATE_BEFORE_NOW = "error.reservation.date.before.now";

    public ReservationDateBeforeNowException() {
        super(KEY_RESERVATION_DATE_BEFORE_NOW);
    }

    public ReservationDateBeforeNowException(Throwable cause) {
        super(KEY_RESERVATION_DATE_BEFORE_NOW, cause);
    }
}
