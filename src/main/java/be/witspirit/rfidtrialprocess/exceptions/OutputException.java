package be.witspirit.rfidtrialprocess.exceptions;

/**
 * Exception to mark output issues
 */
public class OutputException extends RuntimeException {

    public OutputException() {
    }

    public OutputException(String message) {
        super(message);
    }

    public OutputException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutputException(Throwable cause) {
        super(cause);
    }
}
