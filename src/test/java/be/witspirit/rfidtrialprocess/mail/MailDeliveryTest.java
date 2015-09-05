package be.witspirit.rfidtrialprocess.mail;

import be.witspirit.rfidtrialprocess.config.TestConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.nio.file.Paths;

/**
 * Tests mail delivery
 */
@ContextConfiguration(classes = {TestConfig.class})
public class MailDeliveryTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private TestConfig testConfig;

    @Test
    @Ignore("Not a real test as it will effectively send a mail, but doesn't check the outcome")
    public void performSampleDelivery() {
        MailSender sender = new GMailSender(testConfig.mailAccountUsername(), testConfig.mailAccountPassword());
        MailMessage message = new TosInstructionMessage(testConfig.mailFrom(), testConfig.mailTo(), Paths.get("src/test/resources/expectedTrialInstructions.txt"));

        sender.deliver(message);
    }
}
