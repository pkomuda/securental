package pl.lodz.p.it.securental.exceptions;

public abstract class ApplicationBaseException extends Exception {

    public static final String KEY_DEFAULT = "error.default";

    public ApplicationBaseException() {
        super(KEY_DEFAULT);
    }

    public ApplicationBaseException(String message) {
        super(message);
    }

    public ApplicationBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationBaseException(Throwable cause) {
        super(KEY_DEFAULT, cause);
    }
}
