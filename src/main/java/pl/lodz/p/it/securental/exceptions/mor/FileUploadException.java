package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class FileUploadException extends ApplicationBaseException {

    public static final String KEY_FILE_UPLOAD = "error.file.upload";

    public FileUploadException() {
        super(KEY_FILE_UPLOAD);
    }

    public FileUploadException(Throwable cause) {
        super(KEY_FILE_UPLOAD, cause);
    }
}
