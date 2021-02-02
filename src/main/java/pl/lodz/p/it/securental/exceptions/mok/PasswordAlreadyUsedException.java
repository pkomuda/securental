package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class PasswordAlreadyUsedException extends ApplicationBaseException {

    public static final String KEY_PASSWORD_ALREADY_USED = "error.password.already.used";

    public PasswordAlreadyUsedException() {
        super(KEY_PASSWORD_ALREADY_USED);
    }

    public PasswordAlreadyUsedException(Throwable cause) {
        super(KEY_PASSWORD_ALREADY_USED, cause);
    }
}
