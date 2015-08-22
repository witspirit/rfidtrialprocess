package be.witspirit.rfidtrialprocess.mail;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

/**
 * Implementation of the MailSender that uses GMail as the SMTP server
 */
public class GMailSender implements MailSender {

    private final Session session;

    public GMailSender(String username, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

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
