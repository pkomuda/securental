package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class ReservationNumberNotMatchingException extends ApplicationBaseException {

    public static final String KEY_RESERVATION_NUMBER_NOTMATCHING = "error.reservation.number.notmatching";

    public ReservationNumberNotMatchingException() {
        super(KEY_RESERVATION_NUMBER_NOTMATCHING);
    }

    public ReservationNumberNotMatchingException(Throwable cause) {
        super(KEY_RESERVATION_NUMBER_NOTMATCHING, cause);
    }
}
