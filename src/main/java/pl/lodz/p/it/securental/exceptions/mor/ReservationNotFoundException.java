package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class ReservationNotFoundException extends ApplicationBaseException {

    public static final String KEY_RESERVATION_NOT_FOUND = "error.reservation.notfound";

    public ReservationNotFoundException() {
        super(KEY_RESERVATION_NOT_FOUND);
    }

    public ReservationNotFoundException(Throwable cause) {
        super(KEY_RESERVATION_NOT_FOUND, cause);
    }
}
