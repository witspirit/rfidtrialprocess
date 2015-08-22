package be.witspirit.rfidtrialprocess.mail;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Tests mail delivery
 */
public class MailDeliveryTest {

    // TODO Read the following from properties
    private String accountUsername = "dummyUsername";
    private String accountPassword = "dummyPassword";

    private String fromAddress = "dummyFrom@example.com";
    private String toAddress = "dummyTo@example.com";

    @Test
    @Ignore("Not a real test as it will effectively send a mail, but doesn't check the outcome")
    public void performSampleDelivery() {
        MailSender sender = new GMailSender(accountUsername, accountPassword);
        MailMessage message = new TosInstructionMessage(fromAddress, toAddress, Paths.get("src/test/resources/expectedTrialInstructions.txt"));

        sender.deliver(message);
    }
}
