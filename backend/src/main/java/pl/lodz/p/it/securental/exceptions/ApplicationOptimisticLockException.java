package pl.lodz.p.it.securental.exceptions;

public class ApplicationOptimisticLockException extends ApplicationBaseException {

    public static final String KEY_OPTIMISTIC_LOCK = "error.optimistic.lock";

    public ApplicationOptimisticLockException() {
        super(KEY_OPTIMISTIC_LOCK);
    }

    public ApplicationOptimisticLockException(Throwable cause) {
        super(KEY_OPTIMISTIC_LOCK, cause);
    }
}
