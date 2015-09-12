package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidScan;
import be.witspirit.rfidtrialprocess.tos.TosInstruction;
import be.witspirit.rfidtrialprocess.tos.TosOutputProducer;
import be.witspirit.rfidtrialprocess.tos.TosTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    private FileProcessor fileProcessor;

    public RfidProcessor(Path inputDir, Path outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;

        this.fileProcessor = new FileProcessor(inputDir);
        this.fileProcessor.register(this::process);
    }

    public void handle(Predicate<String> fileNameFilter, TosTransformer tosTransformer) {
        handlers.add(new HandlerRegistration(fileNameFilter, tosTransformer));
    }

    public void listenForEvents() {
        fileProcessor.startWatch();
    }

    public void processInputDir() {
        this.fileProcessor.scan();
    }

    private void process(Path filePath) {
        Path fileName = filePath.getFileName();
        LOG.info("Processing "+fileName);

        selectTransformer(fileName).ifPresent(transformer -> produceOutput(fileName, transformer));
    }

    private void produceOutput(Path filePath, TosTransformer transformer) {
        Path inputFilePath = inputDir.resolve(filePath);
        try (FileReader inputReader = openReader(inputFilePath)){
            List<PhiDataRfidScan> scans = new PhiDataRfidInputParser().readFrom(inputReader);
            List<TosInstruction> instructions = transformer.scansToTos(scans, RfidProcessor::phiDataVinExtractor);
            Path outputFilePath = outputDir.resolve(filePath.toString() + "_INSTRUCTIONS.txt");
            new TosOutputProducer().write(instructions, new FileWriter(outputFilePath.toFile()));
        } catch (Exception e) {
            // TODO Should be cleaner/more explicit...
            throw new RuntimeException("Something went wrong producing the output", e);
        }
    }

    public static String phiDataVinExtractor(PhiDataRfidScan scan) {
        return new String(scan.getUid(), StandardCharsets.US_ASCII);
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
