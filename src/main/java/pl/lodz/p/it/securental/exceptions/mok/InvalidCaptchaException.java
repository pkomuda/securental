package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class InvalidCaptchaException extends ApplicationBaseException {

    public static final String KEY_CAPTCHA_INVALID = "error.captcha.invalid";

    public InvalidCaptchaException() {
        super(KEY_CAPTCHA_INVALID);
    }

    public InvalidCaptchaException(Throwable cause) {
        super(KEY_CAPTCHA_INVALID, cause);
    }
}
