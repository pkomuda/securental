package pl.lodz.p.it.securental.exceptions.accounts;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class QrCodeGenerationException extends ApplicationBaseException {

    public static final String KEY_QRCODE_GENERATION = "error.qrcode.generation";

    public QrCodeGenerationException() {
        super(KEY_QRCODE_GENERATION);
    }

    public QrCodeGenerationException(Throwable cause) {
        super(KEY_QRCODE_GENERATION, cause);
    }
}
