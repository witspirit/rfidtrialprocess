package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.file.FileConverter;
import be.witspirit.rfidtrialprocess.file.FileNameMappers;
import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.file.FilteredDelegate;
import be.witspirit.rfidtrialprocess.output.VinWriter;
import be.witspirit.rfidtrialprocess.rfidscan.VinParser;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The processor will listen for new files in an input directory and pick them up
 * and create transformed TOS Instruction files in an output directory
 */
public class RfidProcessor {
    private final Path outputDir;

    private FileProcessor fileProcessor;

    public RfidProcessor(Path inputDir, Path outputDir) {
        this.outputDir = outputDir;

        this.fileProcessor = new FileProcessor(inputDir);
    }

    public void handle(Predicate<Path> pathFilter, VinParser vinParser, VinWriter vinWriter) {
        fileProcessor.register(new FilteredDelegate(pathFilter, new FileConverter<>(
                vinParser::parse,
                FileNameMappers.toDir(outputDir).andThen(FileNameMappers.suffix("_INSTRUCTIONS.txt")),
                vinWriter::write
        )));
    }

    public void listenForEvents() {
        fileProcessor.startWatch();
    }

    public void processInputDir() {
        this.fileProcessor.scan();
    }

}
