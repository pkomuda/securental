package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class TokenAlreadyUsedException extends ApplicationBaseException {

    public static final String KEY_TOKEN_ALREADY_USED = "error.token.already.used";

    public TokenAlreadyUsedException() {
        super(KEY_TOKEN_ALREADY_USED);
    }

    public TokenAlreadyUsedException(Throwable cause) {
        super(KEY_TOKEN_ALREADY_USED, cause);
    }
}
