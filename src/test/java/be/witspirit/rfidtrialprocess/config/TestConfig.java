package be.witspirit.rfidtrialprocess.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * Spring Java Config that loads the test environment properties
 */
@Configuration
@PropertySources({
        @PropertySource(value = { "config/test.properties" }),
        @PropertySource(value = { "config/test.private.properties" }, ignoreResourceNotFound = true)
})
public class TestConfig {

    @Autowired
    private Environment env;

    // Make the environment available via values
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // Could use value annotations instead, but this seems more explicit... Although it perhaps creates more coupling...

    @Bean
    public String mailAccountUsername() {
        return env.getProperty("mail.account.username");
    }

    @Bean
    public String mailAccountPassword() {
        return env.getProperty("mail.account.password");
    }

    @Bean
    public String mailFrom() {
        return env.getProperty("mail.from");
    }

    @Bean
    public String mailTo() {
        return env.getProperty("mail.to");
    }
}
