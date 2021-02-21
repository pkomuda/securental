package pl.lodz.p.it.securental.exceptions.mor;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public class EmployeeOwnReservationException extends ApplicationBaseException {

    public static final String KEY_EMPLOYEE_OWN_RESERVATION = "error.employee.own.reservation";

    public EmployeeOwnReservationException() {
        super(KEY_EMPLOYEE_OWN_RESERVATION);
    }

    public EmployeeOwnReservationException(Throwable cause) {
        super(KEY_EMPLOYEE_OWN_RESERVATION, cause);
    }
}
