package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class PasswordsNotMatchingException extends ApplicationBaseException {

    public static final String KEY_PASSWORDS_NOTMATCHING = "error.passwords.notmatching";

    public PasswordsNotMatchingException() {
        super(KEY_PASSWORDS_NOTMATCHING);
    }

    public PasswordsNotMatchingException(Throwable cause) {
        super(KEY_PASSWORDS_NOTMATCHING, cause);
    }
}
