package pl.lodz.p.it.securental.exceptions.accounts;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class IncorrectCredentialsException extends ApplicationBaseException {

    public static final String KEY_INCORRECT_CREDENTIALS = "error.account.credentials";

    public IncorrectCredentialsException() {
        super(KEY_INCORRECT_CREDENTIALS);
    }

    public IncorrectCredentialsException(Throwable cause) {
        super(KEY_INCORRECT_CREDENTIALS, cause);
    }
}
