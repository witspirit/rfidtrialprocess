package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.trial.Configurations;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Unit test for the RFID Processing infrastructure
 */
public class RfidProcessorTest {
    private static final Logger LOG = LoggerFactory.getLogger(RfidProcessorTest.class);

    private static final Path rootDir = Paths.get("build", "processortest");

    private FileProcessor configureRfidProcessor(Path input, Path output, Path processed) {
        return Configurations.phiDataTest(input, output, processed);
    }

    @Test
    public void fullSetup() throws IOException, ExecutionException, InterruptedException {
        // Ensure we start clean
        Path input = setupDir("watchInput");
        Path output = setupDir("watchOutput");
        Path processed = setupDir("watchProcessed");

        FileProcessor processor = configureRfidProcessor(input, output, processed);

        Future<?> listener = Executors.newSingleThreadExecutor().submit(processor::startWatch);
        LOG.debug("Submitted listenForEvents");

        Thread.sleep(5000); // Give the other thread some time to kick in

        // Copy some files to the input directory...
        Path rfidSample = Paths.get("src", "test", "resources", "phidata/BRMLog_D2015-22-06_T104026.csv");
        Files.copy(rfidSample, input.resolve("ARR_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("WASH_sample.csv"), StandardCopyOption.REPLACE_EXISTING);

        LOG.debug("Copied first 2 files.");
        Thread.sleep(5000); // Give it some time to detect the first 2 files

        Files.copy(rfidSample, input.resolve("DEP_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("Unhandled_sample.csv"), StandardCopyOption.REPLACE_EXISTING);

        LOG.debug("Copied remaining files.");
        Thread.sleep(10000); // Some more wait time for processing the files

        // Check output files
        Assert.assertTrue(Files.exists(output.resolve("ARR_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("WASH_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("DEP_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertFalse(Files.exists(output.resolve("Unhandled_sample.csv_INSTRUCTIONS.txt")));

        // Check processed files
        Assert.assertTrue(Files.exists(processed.resolve("ARR_sample.csv")));
        Assert.assertTrue(Files.exists(processed.resolve("WASH_sample.csv")));
        Assert.assertTrue(Files.exists(processed.resolve("DEP_sample.csv")));
        Assert.assertFalse(Files.exists(processed.resolve("Unhandled_sample.csv")));

        // Check input files have moved
        Assert.assertFalse(Files.exists(input.resolve("ARR_sample.csv")));
        Assert.assertFalse(Files.exists(input.resolve("WASH_sample.csv")));
        Assert.assertFalse(Files.exists(input.resolve("DEP_sample.csv")));
        Assert.assertTrue(Files.exists(input.resolve("Unhandled_sample.csv")));

        // Delete the input directory to end the listener
        deleteDir(input);

        // And then we await the result
        LOG.debug("Awaiting event listener termination...");
        listener.get();
    }



    @Test
    public void directoryScan() throws IOException {
        Path input = setupDir("scanInput");
        Path output = setupDir("scanOutput");
        Path processed = setupDir("scanProcessed");

        // Path rfidSample = Paths.get("src", "test", "resources", "BRMLog_D2015-22-06_T104026.csv");
        Path rfidSample = Paths.get("src", "test", "resources", "phidata/mazdaLikeSample.csv");
        Files.copy(rfidSample, input.resolve("ARR_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("WASH_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("DEP_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("Unhandled_sample.csv"), StandardCopyOption.REPLACE_EXISTING);

        FileProcessor processor = configureRfidProcessor(input, output, processed);

        processor.scan();

        // Check output files
        Assert.assertTrue(Files.exists(output.resolve("ARR_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("WASH_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("DEP_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertFalse(Files.exists(output.resolve("Unhandled_sample.csv_INSTRUCTIONS.txt")));

        // Check processed files
        Assert.assertTrue(Files.exists(processed.resolve("ARR_sample.csv")));
        Assert.assertTrue(Files.exists(processed.resolve("WASH_sample.csv")));
        Assert.assertTrue(Files.exists(processed.resolve("DEP_sample.csv")));
        Assert.assertFalse(Files.exists(processed.resolve("Unhandled_sample.csv")));

        // Check input files have moved
        Assert.assertFalse(Files.exists(input.resolve("ARR_sample.csv")));
        Assert.assertFalse(Files.exists(input.resolve("WASH_sample.csv")));
        Assert.assertFalse(Files.exists(input.resolve("DEP_sample.csv")));
        Assert.assertTrue(Files.exists(input.resolve("Unhandled_sample.csv")));

    }

    private Path setupDir(String dirName) {
        try {
            Path dir = rootDir.resolve(dirName);
            deleteDir(dir);
            return Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare test directory "+dirName, e);
        }
    }

    private void deleteDir(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    LOG.debug("Deleting " + file);
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
            LOG.debug("Deleting " + dir);
            Files.delete(dir);
        }
    }
}
