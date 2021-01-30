package pl.lodz.p.it.securental.exceptions.mok;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class InvalidOtpCodeException extends ApplicationBaseException {

    public static final String KEY_OTP_CODE_INVALID = "error.otp.code.invalid";

    public InvalidOtpCodeException() {
        super(KEY_OTP_CODE_INVALID);
    }

    public InvalidOtpCodeException(Throwable cause) {
        super(KEY_OTP_CODE_INVALID, cause);
    }
}
