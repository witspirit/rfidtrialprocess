package be.witspirit.rfidtrialprocess.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Describes an e-mail with a TOS Instruction file as attachment
 */
public class TosInstructionMessage implements MailMessage {

    private final String from;
    private final String to;
    private final Path filename;

    public TosInstructionMessage(String from, String to, Path filename) {
        this.from = from;
        this.to = to;
        this.filename = filename;
    }

    @Override
    public Message toJavaMessage(Session mailSession) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("RFID Based TOS Instructions Available");

            String messageContent = "Hi, \n\nPlease find in attachment a new TOS instructions file, based on detected RFIDs.\n\nKind regards, \n\n\tThe RFID trial team\n";
            // message.setText(messageContent); // Only for simple messages this convenience method can be used. For multi-parts, you have to wrap it

            MimeBodyPart text = new MimeBodyPart();
            text.setContent(messageContent, "text/plain");

            MimeBodyPart fileAttachment = new MimeBodyPart();
            // New JavaMail 1.4 style
            fileAttachment.attachFile(filename.toFile());

            // Old JavaMail < 1.4 style
//            DataSource source = new FileDataSource(filename.toFile());
//            fileAttachment.setDataHandler(new DataHandler(source));
//            fileAttachment.setFileName(filename.getFileName().toString());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(text);
            multipart.addBodyPart(fileAttachment);

            message.setContent(multipart);

            return message;
        } catch (MessagingException | IOException e) {
            throw new MailDeliveryException("Failed to construct mail message", e);
        }
    }
}
