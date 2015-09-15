package be.witspirit.rfidtrialprocess.trial;

import be.witspirit.rfidtrialprocess.file.*;
import be.witspirit.rfidtrialprocess.output.tos.TrialInstructions;
import be.witspirit.rfidtrialprocess.rfidscan.VinParser;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
import be.witspirit.rfidtrialprocess.rfidscan.vilant.VilantRfidInputParser;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helps to define some configurations
 */
public class Configurations {

    public static FileProcessor phiDataTest(Path input, Path output, Path processed) {
        return common(input, output, processed, new PhiDataRfidInputParser());
    }

    public static FileProcessor vilantTest(Path input, Path output, Path processed) {
        return common(input, output, processed, new VilantRfidInputParser());
    }

    public static FileProcessor vilantTrial(String trialInstruction, Path input, Path output, Path processed, BiConsumer<Path, Path>... postProcessors) {
        FileProcessor processor = new FileProcessor(input);
        ProcessorFactory rfidProcessor = new ProcessorFactory()
                .setNameMapper(FileNameMappers.toDir(output).andThen(FileNameMappers.suffix("_INSTRUCTIONS.txt")))
                .setParser(new VilantRfidInputParser())
                .addPostProcessors(new FileMover(processed))
                .addPostProcessors(postProcessors);

        processor.register(rfidProcessor.forIntruction(trialInstruction));
        return processor;
    }

    public static FileProcessor common(Path input, Path output, Path processed, VinParser vinParser, BiConsumer<Path, Path>... postProcessors) {
        FileProcessor processor = new FileProcessor(input);
        ProcessorFactory rfidProcessor = new ProcessorFactory()
                .setNameMapper(FileNameMappers.toDir(output).andThen(FileNameMappers.suffix("_INSTRUCTIONS.txt")))
                .setParser(vinParser)
                .addPostProcessors(new FileMover(processed))
                .addPostProcessors(postProcessors);

        processor.register(handler("ARR", rfidProcessor.forIntruction(TrialInstructions.ARRIVAL)));
        processor.register(handler("WASH", rfidProcessor.forIntruction(TrialInstructions.VPC_DONE)));
        processor.register(handler("DEP", rfidProcessor.forIntruction(TrialInstructions.DEPARTURE)));
        return processor;
    }

    private static Consumer<Path> handler(String prefix, Consumer<Path> rfidHandler) {
        return new FilteredDelegate(
                PathFilters.fileNameStartsWith(prefix),
                rfidHandler
        );
    }
}
