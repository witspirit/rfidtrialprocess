package be.witspirit.rfidtrialprocess.tools;

import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.trial.Configurations;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Not really a test, but a skeleton to run RFID processing during the trial
 */
@Ignore("Not really a test... Apparently gradle does try to it.")
public class TrialRunner {

    private static final Path READ_DIR = Paths.get("D:\\Users\\bvanvlerken\\RealDocs\\RFID4VehicleLogistics\\Trial\\ScanFiles");
    private static final Path WRITE_DIR = Paths.get("D:\\Users\\bvanvlerken\\RealDocs\\RFID4VehicleLogistics\\Trial\\TosInstructions");
    private static final Path PROCESSED_DIR = Paths.get("D:\\Users\\bvanvlerken\\RealDocs\\RFID4VehicleLogistics\\Trial\\ProcessedScanFiles"); //TODO

    private FileProcessor processor;

    @Before
    public void configureProcessor() {
        processor = Configurations.vilantTrial(READ_DIR, WRITE_DIR);
    }

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
