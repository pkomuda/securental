package pl.lodz.p.it.securental.exceptions.database;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class DataProcessingException extends ApplicationBaseException {

    public static final String KEY_DATA_PROCESSING = "error.data.processing";

    public DataProcessingException() {
        super(KEY_DATA_PROCESSING);
    }

    public DataProcessingException(Throwable cause) {
        super(KEY_DATA_PROCESSING, cause);
    }
}
