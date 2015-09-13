package be.witspirit.rfidtrialprocess.mail;

import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * Delivers the output file by e-mail
 */
public class MailDeliveryPostProcessor implements BiConsumer<Path, Path> {

    private MailSender mailSender;
    private String from;
    private String to;

    public MailDeliveryPostProcessor(MailSender mailSender, String from, String to) {
        this.mailSender = mailSender;
        this.from = from;
        this.to = to;
    }

    @Override
    public void accept(Path inputPath, Path outputPath) {
        mailSender.deliver(new TosInstructionMessage(from, to, outputPath));
    }
}
