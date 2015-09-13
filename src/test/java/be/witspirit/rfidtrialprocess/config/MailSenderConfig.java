package be.witspirit.rfidtrialprocess.config;

import be.witspirit.rfidtrialprocess.mail.GMailSender;
import be.witspirit.rfidtrialprocess.mail.MailSender;
import be.witspirit.rfidtrialprocess.mail.GenericMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

/**
 * Spring Java Config that loads the test environment properties
 */
@Configuration
@PropertySources({
        @PropertySource(value = { "config/mailsender.properties" }),
        @PropertySource(value = { "config/mailsender.private.properties" }, ignoreResourceNotFound = true)
})
public class MailSenderConfig {

    @Autowired
    private Environment env;

    @Bean
    public String mailAccountUsername() {
        return env.getProperty("mail.account.username");
    }

    @Bean
    public String mailAccountPassword() {
        return env.getProperty("mail.account.password");
    }

    @Bean
    public String smptpHost() {
        return env.getProperty("mail.smtp.host");
    }

    @Bean
    public String smtpPort() {
        return env.getProperty("mail.smtp.port");
    }

    @Bean
    public MailSender mailSender() {
        String senderType = env.getProperty("mail.sender.type", "generic");
        if (senderType.equals("gmail")) {
            return new GMailSender(mailAccountUsername(), mailAccountPassword());
        } else if (senderType.equals("generic")) {
            return new GenericMailSender(smptpHost(), smtpPort());
        } else {
            throw new IllegalArgumentException("Don't know of a sender type named "+senderType);
        }
    }
}
