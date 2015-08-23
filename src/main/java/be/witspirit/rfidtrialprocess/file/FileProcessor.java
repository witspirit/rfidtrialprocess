package be.witspirit.rfidtrialprocess.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The file processor allows to watch or scan a directory for files
 */
public class FileProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(FileProcessor.class);

    private final Path inputDir;

    private List<Consumer<Path>> consumers = new ArrayList<>();

    public FileProcessor(Path inputDir) {
        this.inputDir = inputDir;
    }

    public void register(Consumer<Path> fileConsumer) {
        consumers.add(fileConsumer);
    }

    public void startWatch() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = inputDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            LOG.info("File system watch started on "+inputDir+"...");
            while (watchKey.isValid()) {
                try {
                    watchKey = watcher.take();
                } catch (InterruptedException e) {
                    LOG.info("File system watch was interrupted. Aborting file system watch...", e);
                    return;
                }

                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> eventKind = event.kind();

                    if (eventKind == StandardWatchEventKinds.OVERFLOW) {
                        LOG.warn("Event Overflow reported. Possible events missed !");
                    } else {
                        // Proper event
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        Path filePath = pathEvent.context();
                        LOG.info(eventKind+" event detected for "+filePath);

                        // We have to resolve to obtain a full path, which is in line with the behavior of the scan
                        process(inputDir.resolve(filePath));
                    }
                }

                watchKey.reset();
            }

            LOG.info("File system watch ended as "+inputDir+" became unavailable.");


        } catch (IOException e) {
            throw new RuntimeException("Failed to observe the filesystem using a WatchService", e);
        }

    }

    public void scan() {
        try {
            Files.list(inputDir).forEach(this::process);
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files from input directory.");
        }

    }

    private void process(Path filePath) {
        Path fileName = filePath.getFileName();
        LOG.info("Processing "+fileName);

        consumers.forEach(c -> c.accept(filePath));
    }
}
