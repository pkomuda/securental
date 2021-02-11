package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class ReservationImagesAmountIncorrectException extends ApplicationBaseException {

    public static final String KEY_RESERVATION_IMAGES_AMOUNT_INCORRECT = "error.reservation.images.amount";

    public ReservationImagesAmountIncorrectException() {
        super(KEY_RESERVATION_IMAGES_AMOUNT_INCORRECT);
    }

    public ReservationImagesAmountIncorrectException(Throwable cause) {
        super(KEY_RESERVATION_IMAGES_AMOUNT_INCORRECT, cause);
    }
}
