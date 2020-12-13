package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class UsernameNotMatchingException extends ApplicationBaseException {

    public static final String KEY_USERNAME_NOTMATCHING = "error.username.notmatching";

    public UsernameNotMatchingException() {
        super(KEY_USERNAME_NOTMATCHING);
    }

    public UsernameNotMatchingException(Throwable cause) {
        super(KEY_USERNAME_NOTMATCHING, cause);
    }
}
