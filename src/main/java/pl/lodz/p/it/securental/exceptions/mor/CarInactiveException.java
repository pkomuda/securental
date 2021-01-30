package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class CarInactiveException extends ApplicationBaseException {

    public static final String KEY_CAR_INACTIVE = "error.car.inactive";

    public CarInactiveException() {
        super(KEY_CAR_INACTIVE);
    }

    public CarInactiveException(Throwable cause) {
        super(KEY_CAR_INACTIVE, cause);
    }
}
