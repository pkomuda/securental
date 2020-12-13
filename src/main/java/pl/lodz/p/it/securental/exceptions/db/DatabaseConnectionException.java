package pl.lodz.p.it.securental.exceptions.db;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class DatabaseConnectionException extends ApplicationBaseException {

    public static final String KEY_DATABASE_CONNECTION = "error.db.connection";

    public DatabaseConnectionException() {
        super(KEY_DATABASE_CONNECTION);
    }

    public DatabaseConnectionException(Throwable cause) {
        super(KEY_DATABASE_CONNECTION, cause);
    }
}
