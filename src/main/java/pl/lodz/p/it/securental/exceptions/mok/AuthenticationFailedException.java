package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class AuthenticationFailedException extends ApplicationBaseException {

    public static final String KEY_AUTHENTICATION_FAILED = "error.credentials.incorrect";

    public AuthenticationFailedException() {
        super(KEY_AUTHENTICATION_FAILED);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(KEY_AUTHENTICATION_FAILED, cause);
    }
}
