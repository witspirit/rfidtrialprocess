package be.witspirit.rfidtrialprocess.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * Delivers the output file by e-mail
 */
public class MailDeliveryPostProcessor implements BiConsumer<Path, Path> {
    private static final Logger LOG = LoggerFactory.getLogger(MailDeliveryPostProcessor.class);

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
        LOG.info("Delivered "+outputPath+" to "+to);
    }
}
