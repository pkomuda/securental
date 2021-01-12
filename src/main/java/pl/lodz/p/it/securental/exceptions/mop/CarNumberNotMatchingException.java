package pl.lodz.p.it.securental.exceptions.mop;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class CarNumberNotMatchingException extends ApplicationBaseException {

    public static final String KEY_CAR_NUMBER_NOTMATCHING = "error.car.number.notmatching";

    public CarNumberNotMatchingException() {
        super(KEY_CAR_NUMBER_NOTMATCHING);
    }

    public CarNumberNotMatchingException(Throwable cause) {
        super(KEY_CAR_NUMBER_NOTMATCHING, cause);
    }
}
