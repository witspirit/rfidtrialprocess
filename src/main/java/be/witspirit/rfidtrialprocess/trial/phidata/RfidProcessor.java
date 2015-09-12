package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.file.FileConverter;
import be.witspirit.rfidtrialprocess.file.FileNameMappers;
import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.output.VinWriter;
import be.witspirit.rfidtrialprocess.rfidscan.VinParser;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
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

    private final Path outputDir;

    private List<HandlerRegistration> handlers = new ArrayList<>();

    private FileProcessor fileProcessor;
    private VinParser vinParser;

    public RfidProcessor(Path inputDir, Path outputDir) {
        this.outputDir = outputDir;

        this.fileProcessor = new FileProcessor(inputDir);
        this.fileProcessor.register(this::process);

        this.vinParser = new PhiDataRfidInputParser();
    }

    public void handle(Predicate<String> fileNameFilter, VinWriter vinWriter) {
        handlers.add(new HandlerRegistration(fileNameFilter, vinWriter));
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

        selectTransformer(fileName).ifPresent(transformer -> produceOutput(filePath, transformer));
    }

    private void produceOutput(Path filePath, VinWriter vinWriter) {
        new FileConverter<>(
                vinParser::parse,
                FileNameMappers.toDir(outputDir).andThen(FileNameMappers.suffix("_INSTRUCTIONS.txt")),
                vinWriter::write
                ).accept(filePath);
    }

    private Optional<VinWriter> selectTransformer(Path filePath) {
        for (HandlerRegistration handler : handlers) {
            if (handler.fileNameFilter.test(filePath.toString())) {
                LOG.debug("Found handler for "+filePath);
                return Optional.of(handler.vinWriter);
            }
        }
        LOG.debug("No handler found for "+filePath);
        return Optional.empty();
    }


    private static class HandlerRegistration {
        private Predicate<String> fileNameFilter;
        private VinWriter vinWriter;

        public HandlerRegistration(Predicate<String> fileNameFilter, VinWriter vinWriter) {
            this.fileNameFilter = fileNameFilter;
            this.vinWriter = vinWriter;
        }
    }
}
