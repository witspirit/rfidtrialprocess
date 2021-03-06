package be.witspirit.rfidtrialprocess.exceptions;

/**
 * Exception to mark input issues
 */
public class InputException extends RuntimeException {

    public InputException() {
    }

    public InputException(String message) {
        super(message);
    }

    public InputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputException(Throwable cause) {
        super(cause);
    }
}
