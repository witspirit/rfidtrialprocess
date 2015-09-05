package be.witspirit.rfidtrialprocess.file;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Unit test for the FileProcessor
 */
public class FileProcessorTest {
    private static final Logger LOG = LoggerFactory.getLogger(FileProcessorTest.class);

    private static final Path rootDir = Paths.get("build", "processortest");

    @Test
    public void directoryWatch() throws IOException, ExecutionException, InterruptedException {
        // Ensure we start clean
        Path input = rootDir.resolve("watchInput");
        deleteDir(input);
        input = Files.createDirectories(input);

        FileProcessor processor = new FileProcessor(input);
        TestFileConsumer fileConsumer = new TestFileConsumer();
        processor.register(fileConsumer);

        Future<?> listener = Executors.newSingleThreadExecutor().submit(processor::startWatch);
        LOG.debug("Submitted startWatch");

        Thread.sleep(5000); // Give the other thread some time to kick in

        // Copy some files to the input directory...
        Path rfidSample = Paths.get("src", "test", "resources", "BRMLog_D2015-22-06_T104026.csv");

        Path inputPath1 = input.resolve("ARR_sample.csv");
        Path inputPath2 = input.resolve("WASH_sample.csv");
        Path inputPath3 = input.resolve("DEP_sample.csv");
        Path inputPath4 = input.resolve("Unhandled_sample.csv");

        Files.copy(rfidSample, inputPath1, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, inputPath2, StandardCopyOption.REPLACE_EXISTING);

        LOG.debug("Copied first 2 files.");
        Thread.sleep(5000); // Give it some time to detect the first 2 files

        Files.copy(rfidSample, inputPath3, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, inputPath4, StandardCopyOption.REPLACE_EXISTING);

        LOG.debug("Copied remaining files.");
        Thread.sleep(10000); // Some more wait time for processing the files

        // Delete the input directory to end the listener
        deleteDir(input);

        // And then we await the result
        LOG.debug("Awaiting event listener termination...");
        listener.get();

        // Check received paths
        List<Path> receivedPaths = fileConsumer.getReceivedPaths();
        // Since it is uncertain whether we would also get modify events, we are going to relax our
        // test and just consider the unique paths received
        Set<Path> uniquePaths = new HashSet<>(receivedPaths);
        Assert.assertEquals(4, uniquePaths.size());
        Assert.assertTrue(uniquePaths.contains(inputPath1));
        Assert.assertTrue(uniquePaths.contains(inputPath2));
        Assert.assertTrue(uniquePaths.contains(inputPath3));
        Assert.assertTrue(uniquePaths.contains(inputPath4));
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

        Path rfidSample = Paths.get("src", "test", "resources", "BRMLog_D2015-22-06_T104026.csv");
        Path inputPath1 = input.resolve("ARR_sample.csv");
        Path inputPath2 = input.resolve("WASH_sample.csv");
        Path inputPath3 = input.resolve("DEP_sample.csv");
        Path inputPath4 = input.resolve("Unhandled_sample.csv");

        Files.copy(rfidSample, inputPath1, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, inputPath2, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, inputPath3, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(rfidSample, inputPath4, StandardCopyOption.REPLACE_EXISTING);

        FileProcessor processor = new FileProcessor(input);
        TestFileConsumer fileConsumer = new TestFileConsumer();
        processor.register(fileConsumer);

        processor.scan();

        // Check received paths
        List<Path> receivedPaths = fileConsumer.getReceivedPaths();
        Assert.assertEquals(4, receivedPaths.size());

        // Since we have no guarentees on the order in which the files are detected,
        // we are going to relax the test and just check containment in a set
        Set<Path> uniquePaths = new HashSet<>(receivedPaths);
        Assert.assertEquals(4, uniquePaths.size());
        Assert.assertTrue(uniquePaths.contains(inputPath1));
        Assert.assertTrue(uniquePaths.contains(inputPath2));
        Assert.assertTrue(uniquePaths.contains(inputPath3));
        Assert.assertTrue(uniquePaths.contains(inputPath4));
    }

    private static class TestFileConsumer implements Consumer<Path> {

        private List<Path> receivedPaths = new ArrayList<>();

        public List<Path> getReceivedPaths() {
            return receivedPaths;
        }

        @Override
        public void accept(Path path) {
            receivedPaths.add(path);
        }
    }
}
