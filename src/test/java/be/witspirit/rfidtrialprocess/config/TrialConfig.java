package be.witspirit.rfidtrialprocess.config;

import be.witspirit.rfidtrialprocess.file.FileCopier;
import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.mail.MailDeliveryPostProcessor;
import be.witspirit.rfidtrialprocess.output.tos.TrialInstructions;
import be.witspirit.rfidtrialprocess.trial.Configurations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Trial Configuration
 */
@Configuration
@Import(MailSenderConfig.class)
@PropertySources({
        @PropertySource(value = { "config/trial.properties" }),
        @PropertySource(value = { "config/trial.private.properties" }, ignoreResourceNotFound = true)
})
public class TrialConfig {

    @Autowired
    private Environment env;

    @Autowired
    private MailSenderConfig mailConfig;

    @Bean
    public Path inputDir() {
        return Paths.get(env.getProperty("input.dir"));
    }

    @Bean
    public Path outputDir() {
        return Paths.get(env.getProperty("output.dir"));
    }

    @Bean
    public Path processedDir() {
        return Paths.get(env.getProperty("processed.dir"));
    }

    @Bean
    public Path usbDir() {
        return Paths.get(env.getProperty("usb.dir"));
    }

    private MailDeliveryPostProcessor mail(int nr) {
        return new MailDeliveryPostProcessor(mailConfig.mailSender(), env.getProperty("mail.from"), env.getProperty("mail."+nr+".to"));
    }

    @Bean
    public FileCopier usbDrop() {
        return new FileCopier(usbDir());
    }

    @Bean
    public FileProcessor processor() {
        String trialInstruction = TrialInstructions.ARRIVAL;
        // String trialInstruction = TrialInstructions.VPC_DONE;
        // String trialInstruction = TrialInstructions.DEPARTURE;
        return Configurations.vilantTrial(trialInstruction, inputDir(), outputDir(), processedDir(), mail(1), mail(2), mail(3), mail(4));
    }
}
