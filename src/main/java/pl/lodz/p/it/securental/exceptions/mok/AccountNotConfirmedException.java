package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class AccountNotConfirmedException extends ApplicationBaseException {

    public static final String KEY_ACCOUNT_NOT_CONFIRMED = "error.account.not.confirmed";

    public AccountNotConfirmedException() {
        super(KEY_ACCOUNT_NOT_CONFIRMED);
    }

    public AccountNotConfirmedException(Throwable cause) {
        super(KEY_ACCOUNT_NOT_CONFIRMED, cause);
    }
}
