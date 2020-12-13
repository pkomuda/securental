package pl.lodz.p.it.securental.exceptions.db;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class PropertyNotFoundException extends ApplicationBaseException {

    public static final String KEY_PROPERTY_NOT_FOUND = "error.property.notfound";

    public PropertyNotFoundException() {
        super(KEY_PROPERTY_NOT_FOUND);
    }

    public PropertyNotFoundException(Throwable cause) {
        super(KEY_PROPERTY_NOT_FOUND, cause);
    }
}
