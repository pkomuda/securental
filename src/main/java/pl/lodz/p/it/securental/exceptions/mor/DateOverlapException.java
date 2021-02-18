package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class DateOverlapException extends ApplicationBaseException {

    public static final String KEY_DATE_OVERLAP = "error.date.overlap";

    public DateOverlapException() {
        super(KEY_DATE_OVERLAP);
    }

    public DateOverlapException(Throwable cause) {
        super(KEY_DATE_OVERLAP, cause);
    }
}
