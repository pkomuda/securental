package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class IncorrectStatusException extends ApplicationBaseException {

    public static final String KEY_INCORRECT_STATUS = "error.status.incorrect";

    public IncorrectStatusException() {
        super(KEY_INCORRECT_STATUS);
    }

    public IncorrectStatusException(Throwable cause) {
        super(KEY_INCORRECT_STATUS, cause);
    }
}
