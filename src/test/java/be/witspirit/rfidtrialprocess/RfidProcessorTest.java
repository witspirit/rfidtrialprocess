package be.witspirit.rfidtrialprocess;

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
 * Unit test for the RfidProcessor
 */
public class RfidProcessorTest {
    private static final Logger LOG = LoggerFactory.getLogger(RfidProcessorTest.class);

    private static final Path rootDir = Paths.get("build", "processortest");

    @Test
    public void fullSetup() throws IOException, ExecutionException, InterruptedException {
        // Ensure we start clean
        Path input = rootDir.resolve("watchInput");
        deleteDir(input);
        input = Files.createDirectories(input);

        Path output = rootDir.resolve("watchOutput");
        deleteDir(output);
        output = Files.createDirectories(output);

        RfidProcessor processor = new RfidProcessor(input, output);
        processor.handle(fileName -> fileName.startsWith("ARR"), new TosTransformer(TrialInstructions.ARRIVAL));
        processor.handle(fileName -> fileName.startsWith("WASH"), new TosTransformer(TrialInstructions.VPC_DONE));
        processor.handle(fileName -> fileName.startsWith("DEP"), new TosTransformer(TrialInstructions.DEPARTURE));

        Future<?> listener = Executors.newSingleThreadExecutor().submit(processor::listenForEvents);
        LOG.debug("Submitted listenForEvents");

        Thread.sleep(5000); // Give the other thread some time to kick in

        // Copy some files to the input directory...
        Path rfidSample = Paths.get("src", "test", "resources", "BRMLog_D2015-22-06_T104026.csv");
        Files.copy(rfidSample, input.resolve("ARR_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("WASH_sample.csv"), StandardCopyOption.REPLACE_EXISTING);

        LOG.debug("Copied first 2 files.");
        Thread.sleep(5000); // Give it some time to detect the first 2 files

        Files.copy(rfidSample, input.resolve("DEP_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("Unhandled_sample.csv"), StandardCopyOption.REPLACE_EXISTING);

        LOG.debug("Copied remaining files.");
        Thread.sleep(10000); // Some more wait time for processing the files

        // Delete the input directory to end the listener
        deleteDir(input);

        // And then we await the result
        LOG.debug("Awaiting event listener termination...");
        listener.get();

        // Check output files
        Assert.assertTrue(Files.exists(output.resolve("ARR_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("WASH_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("DEP_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertFalse(Files.exists(output.resolve("Unhandled_sample.csv_INSTRUCTIONS.txt")));
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

    @Test
    public void directoryScan() throws IOException {
        Path input = Files.createDirectories(rootDir.resolve("scanInput"));

        Path output = rootDir.resolve("scanOutout");
        deleteDir(output);
        output = Files.createDirectories(output);

        // Path rfidSample = Paths.get("src", "test", "resources", "BRMLog_D2015-22-06_T104026.csv");
        Path rfidSample = Paths.get("src", "test", "resources", "mazdaLikeSample.csv");
        Files.copy(rfidSample, input.resolve("ARR_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("WASH_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("DEP_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("Unhandled_sample.csv"), StandardCopyOption.REPLACE_EXISTING);

        RfidProcessor processor = new RfidProcessor(input, output);
        processor.handle(fileName -> fileName.startsWith("ARR"), new TosTransformer(TrialInstructions.ARRIVAL));
        processor.handle(fileName -> fileName.startsWith("WASH"), new TosTransformer(TrialInstructions.VPC_DONE));
        processor.handle(fileName -> fileName.startsWith("DEP"), new TosTransformer(TrialInstructions.DEPARTURE));

        processor.processInputDir();

        // Check output files
        Assert.assertTrue(Files.exists(output.resolve("ARR_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("WASH_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("DEP_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertFalse(Files.exists(output.resolve("Unhandled_sample.csv_INSTRUCTIONS.txt")));
    }
}
