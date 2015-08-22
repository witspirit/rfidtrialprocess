package be.witspirit.rfidtrialprocess.mail;

/**
 * Exception to indicate issues with MailDelivery
 */
public class MailDeliveryException extends RuntimeException {

    public MailDeliveryException() {
    }

    public MailDeliveryException(String message) {
        super(message);
    }

    public MailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailDeliveryException(Throwable cause) {
        super(cause);
    }
}
