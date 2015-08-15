package be.witspirit.rfidtrialprocess;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Unit test for the RfidProcessor
 */
public class RfidProcessorTest {

    private static final String testRootDir = "build/processortest";
    private static final String input = "input";
    private static final String output = "output";


    private File rootDir;
    private File inputDir;
    private File outputDir;

    @Before
    public void ensureDirectoriesExist() {
        rootDir = new File(testRootDir);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }

        inputDir = new File(rootDir, input);
        if (!inputDir.exists()) {
            inputDir.mkdir();
        }

        outputDir = new File(rootDir, output);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
    }

    @Test
    public void fullSetup() {
        RfidProcessor processor = new RfidProcessor(inputDir, outputDir);
        processor.handle(fileName -> fileName.startsWith("ARR"), new TosTransformer(TrialInstructions.ARRIVAL));
        processor.handle(fileName -> fileName.startsWith("WASH"), new TosTransformer(TrialInstructions.VPC_DONE));
        processor.handle(fileName -> fileName.startsWith("DEP"), new TosTransformer(TrialInstructions.DEPARTURE));

        processor.listenForEvents();

    }
}
