package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.rfidscan.phidata.RfidInputParser;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.RfidScan;
import be.witspirit.rfidtrialprocess.tos.TosInstruction;
import be.witspirit.rfidtrialprocess.tos.TosOutputProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The processor will listen for new files in an input directory and pick them up
 * and create transformed TOS Instruction files in an output directory
 */
public class RfidProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(RfidProcessor.class);

    private final Path inputDir;
    private final Path outputDir;

    private List<HandlerRegistration> handlers = new ArrayList<>();

    public RfidProcessor(Path inputDir, Path outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    public void handle(Predicate<String> fileNameFilter, TosTransformer tosTransformer) {
        handlers.add(new HandlerRegistration(fileNameFilter, tosTransformer));
    }

    public void listenForEvents() {
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

                        process(filePath);
                    }
                }

                watchKey.reset();
            }

            LOG.info("File system watch ended as "+inputDir+" became unavailable.");


        } catch (IOException e) {
            throw new RuntimeException("Failed to observe the filesystem using a WatchService", e);
        }

    }

    public void processInputDir() {
        try {
            Files.list(inputDir).forEach(this::process);
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files from input directory.");
        }

    }

    private void process(Path filePath) {
        Path fileName = filePath.getFileName();
        LOG.info("Processing "+fileName);

        selectTransformer(fileName).ifPresent(transformer -> produceOutput(fileName, transformer));

    }

    private void produceOutput(Path filePath, TosTransformer transformer) {
        Path inputFilePath = inputDir.resolve(filePath);
        try (FileReader inputReader = openReader(inputFilePath)){
            List<RfidScan> scans = new RfidInputParser().readFrom(inputReader);
            List<TosInstruction> instructions = transformer.toTos(scans);
            Path outputFilePath = outputDir.resolve(filePath.toString() + "_INSTRUCTIONS.txt");
            new TosOutputProducer().write(instructions, new FileWriter(outputFilePath.toFile()));
        } catch (Exception e) {
            // TODO Should be cleaner/more explicit...
            throw new RuntimeException("Something went wrong producing the output", e);
        }

    }

    private FileReader openReader(Path inputFilePath) throws FileNotFoundException {
        File file = inputFilePath.toFile();
        for (int i=0; i < 3; i++) {
            try {
                return new FileReader(file);
            } catch (FileNotFoundException fnf) {
                // Might be concurrent access... Back off and try again
                LOG.debug("Received "+fnf.getMessage()+". Sleeping for a while and retrying "+file+" ...");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    LOG.warn("Sleep got interrupted", e);
                }
            }
        }
        LOG.debug("Final attempt at reading "+file);
        return new FileReader(file);
    }

    private Optional<TosTransformer> selectTransformer(Path filePath) {
        for (HandlerRegistration handler : handlers) {
            if (handler.fileNameFilter.test(filePath.toString())) {
                LOG.debug("Found handler for "+filePath);
                return Optional.of(handler.tosTransformer);
            }
        }
        LOG.debug("No handler found for "+filePath);
        return Optional.empty();
    }


    private static class HandlerRegistration {
        private Predicate<String> fileNameFilter;
        private TosTransformer tosTransformer;

        public HandlerRegistration(Predicate<String> fileNameFilter, TosTransformer tosTransformer) {
            this.fileNameFilter = fileNameFilter;
            this.tosTransformer = tosTransformer;
        }
    }
}
