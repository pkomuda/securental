package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class AccountNotFoundException extends ApplicationBaseException {

    public static final String KEY_ACCOUNT_NOT_FOUND = "error.account.notfound";

    public AccountNotFoundException() {
        super(KEY_ACCOUNT_NOT_FOUND);
    }

    public AccountNotFoundException(Throwable cause) {
        super(KEY_ACCOUNT_NOT_FOUND, cause);
    }
}
