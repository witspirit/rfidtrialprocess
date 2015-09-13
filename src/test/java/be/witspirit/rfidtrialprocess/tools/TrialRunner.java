package be.witspirit.rfidtrialprocess.tools;

import be.witspirit.rfidtrialprocess.config.TrialConfig;
import be.witspirit.rfidtrialprocess.file.FileProcessor;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Not really a test, but a skeleton to run RFID processing during the trial
 */
@Ignore("Not really a test... Apparently gradle does try to run it.")
@ContextConfiguration(classes = TrialConfig.class)
public class TrialRunner extends AbstractJUnit4SpringContextTests {


    @Autowired
    private FileProcessor processor;

    @Test
    public void watch() {
        processor.startWatch();
    }

    @Test
    public void scan() {
        processor.scan();
    }

    @Test
    public void file() {
        processor.file("ARR_bla.csv");
    }
}
