package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.file.PathFilters;
import be.witspirit.rfidtrialprocess.output.tos.TosWriter;
import be.witspirit.rfidtrialprocess.output.tos.TrialInstructions;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
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

    private RfidProcessor configureRfidProcessor(Path input, Path output) {
        RfidProcessor processor = new RfidProcessor(input, output);
        processor.handle(PathFilters.fileNameStartsWith("ARR"), new PhiDataRfidInputParser(), new TosWriter(TrialInstructions.ARRIVAL));
        processor.handle(PathFilters.fileNameStartsWith("WASH"), new PhiDataRfidInputParser(), new TosWriter(TrialInstructions.VPC_DONE));
        processor.handle(PathFilters.fileNameStartsWith("DEP"), new PhiDataRfidInputParser(), new TosWriter(TrialInstructions.DEPARTURE));
        return processor;
    }

    @Test
    public void fullSetup() throws IOException, ExecutionException, InterruptedException {
        // Ensure we start clean
        Path input = rootDir.resolve("watchInput");
        deleteDir(input);
        input = Files.createDirectories(input);

        Path output = rootDir.resolve("watchOutput");
        deleteDir(output);
        output = Files.createDirectories(output);

        RfidProcessor processor = configureRfidProcessor(input, output);

        Future<?> listener = Executors.newSingleThreadExecutor().submit(processor::listenForEvents);
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

        Path output = rootDir.resolve("scanOutput");
        deleteDir(output);
        output = Files.createDirectories(output);

        // Path rfidSample = Paths.get("src", "test", "resources", "BRMLog_D2015-22-06_T104026.csv");
        Path rfidSample = Paths.get("src", "test", "resources", "phidata/mazdaLikeSample.csv");
        Files.copy(rfidSample, input.resolve("ARR_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("WASH_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("DEP_sample.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, input.resolve("Unhandled_sample.csv"), StandardCopyOption.REPLACE_EXISTING);

        RfidProcessor processor = configureRfidProcessor(input, output);

        processor.processInputDir();

        // Check output files
        Assert.assertTrue(Files.exists(output.resolve("ARR_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("WASH_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertTrue(Files.exists(output.resolve("DEP_sample.csv_INSTRUCTIONS.txt")));
        Assert.assertFalse(Files.exists(output.resolve("Unhandled_sample.csv_INSTRUCTIONS.txt")));
    }
}
