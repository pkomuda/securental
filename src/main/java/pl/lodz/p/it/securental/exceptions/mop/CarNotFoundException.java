package pl.lodz.p.it.securental.exceptions.mop;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class CarNotFoundException extends ApplicationBaseException {

    public static final String KEY_CAR_NOT_FOUND = "error.car.notfound";

    public CarNotFoundException() {
        super(KEY_CAR_NOT_FOUND);
    }

    public CarNotFoundException(Throwable cause) {
        super(KEY_CAR_NOT_FOUND, cause);
    }
}
