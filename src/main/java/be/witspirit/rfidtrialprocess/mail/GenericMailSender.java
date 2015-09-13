package be.witspirit.rfidtrialprocess.mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

/**
 * Implementation of the MailSender that uses a generic open SMTP server for delivery
 */
public class GenericMailSender implements MailSender {

    private final Session session;

    public GenericMailSender(String smtpHost, String smtpPort) {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        session = Session.getInstance(props);
    }

    @Override
    public void deliver(MailMessage message) {
        try {
            Transport.send(message.toJavaMessage(session));
        } catch (MessagingException e) {
            throw new MailDeliveryException("Failed to send message.", e);
        }
    }
}
