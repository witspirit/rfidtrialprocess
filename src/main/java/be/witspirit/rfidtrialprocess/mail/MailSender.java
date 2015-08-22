package be.witspirit.rfidtrialprocess.mail;

/**
 * Small abstraction for a system to deliver mails
 */
public interface MailSender {

    void deliver(MailMessage message);

}
