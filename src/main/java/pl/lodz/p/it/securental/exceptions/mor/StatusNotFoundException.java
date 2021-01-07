package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class StatusNotFoundException extends ApplicationBaseException {

    public static final String KEY_STATUS_NOT_FOUND = "error.status.notfound";

    public StatusNotFoundException() {
        super(KEY_STATUS_NOT_FOUND);
    }

    public StatusNotFoundException(Throwable cause) {
        super(KEY_STATUS_NOT_FOUND, cause);
    }
}
