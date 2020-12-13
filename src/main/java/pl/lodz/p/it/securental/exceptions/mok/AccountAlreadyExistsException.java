package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class AccountAlreadyExistsException extends ApplicationBaseException {

    public static final String KEY_ACCOUNT_EXISTS = "error.account.exists";

    public AccountAlreadyExistsException() {
        super(KEY_ACCOUNT_EXISTS);
    }

    public AccountAlreadyExistsException(Throwable cause) {
        super(KEY_ACCOUNT_EXISTS, cause);
    }
}
