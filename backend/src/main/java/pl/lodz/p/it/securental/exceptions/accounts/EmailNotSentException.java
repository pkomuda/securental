package pl.lodz.p.it.securental.exceptions.accounts;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class EmailNotSentException extends ApplicationBaseException {

    public static final String KEY_EMAIL_NOT_SENT = "error.email.notsent";

    public EmailNotSentException() {
        super(KEY_EMAIL_NOT_SENT);
    }

    public EmailNotSentException(Throwable cause) {
        super(KEY_EMAIL_NOT_SENT, cause);
    }
}
