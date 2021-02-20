package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class AccountNotActiveException extends ApplicationBaseException {

    public static final String KEY_ACCOUNT_NOT_ACTIVE = "error.account.not.active";

    public AccountNotActiveException() {
        super(KEY_ACCOUNT_NOT_ACTIVE);
    }

    public AccountNotActiveException(Throwable cause) {
        super(KEY_ACCOUNT_NOT_ACTIVE, cause);
    }
}
