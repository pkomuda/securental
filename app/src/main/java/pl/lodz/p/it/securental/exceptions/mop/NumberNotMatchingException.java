package pl.lodz.p.it.securental.exceptions.mop;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class NumberNotMatchingException extends ApplicationBaseException {

    public static final String KEY_NUMBER_NOTMATCHING = "error.number.notmatching";

    public NumberNotMatchingException() {
        super(KEY_NUMBER_NOTMATCHING);
    }

    public NumberNotMatchingException(Throwable cause) {
        super(KEY_NUMBER_NOTMATCHING, cause);
    }
}
