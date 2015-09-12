package be.witspirit.rfidtrialprocess.trial;

import be.witspirit.rfidtrialprocess.file.*;
import be.witspirit.rfidtrialprocess.output.VinWriter;
import be.witspirit.rfidtrialprocess.output.tos.TosWriter;
import be.witspirit.rfidtrialprocess.output.tos.TrialInstructions;
import be.witspirit.rfidtrialprocess.rfidscan.VinParser;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
import be.witspirit.rfidtrialprocess.rfidscan.vilant.VilantRfidInputParser;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helps to define some configurations
 */
public class Configurations {

    public static FileProcessor phiDataTest(Path input, Path output) {
        FileProcessor processor = new FileProcessor(input);
        Function<Path, Path> nameMapper = FileNameMappers.toDir(output).andThen(FileNameMappers.suffix("_INSTRUCTIONS.txt"));
        processor.register(handler("ARR", new PhiDataRfidInputParser(), nameMapper,  new TosWriter(TrialInstructions.ARRIVAL)));
        processor.register(handler("WASH", new PhiDataRfidInputParser(), nameMapper, new TosWriter(TrialInstructions.VPC_DONE)));
        processor.register(handler("DEP", new PhiDataRfidInputParser(), nameMapper, new TosWriter(TrialInstructions.DEPARTURE)));
        return processor;
    }

    public static FileProcessor vilantTest(Path input, Path output) {
        FileProcessor processor = new FileProcessor(input);
        Function<Path, Path> nameMapper = FileNameMappers.toDir(output).andThen(FileNameMappers.suffix("_INSTRUCTIONS.txt"));
        processor.register(handler("ARR", new VilantRfidInputParser(), nameMapper,  new TosWriter(TrialInstructions.ARRIVAL)));
        processor.register(handler("WASH", new VilantRfidInputParser(), nameMapper, new TosWriter(TrialInstructions.VPC_DONE)));
        processor.register(handler("DEP", new VilantRfidInputParser(), nameMapper, new TosWriter(TrialInstructions.DEPARTURE)));
        return processor;
    }

    public static FileProcessor vilantTrial(Path input, Path output) {
        FileProcessor processor = new FileProcessor(input);
        Function<Path, Path> nameMapper = FileNameMappers.toDir(output).andThen(FileNameMappers.suffix("_INSTRUCTIONS.txt"));
        processor.register(handler("ARR", new VilantRfidInputParser(), nameMapper,  new TosWriter(TrialInstructions.ARRIVAL)));
        processor.register(handler("WASH", new VilantRfidInputParser(), nameMapper, new TosWriter(TrialInstructions.VPC_DONE)));
        processor.register(handler("DEP", new VilantRfidInputParser(), nameMapper, new TosWriter(TrialInstructions.DEPARTURE)));
        return processor;
    }

    private static Consumer<Path> handler(String prefix, VinParser parser, Function<Path, Path> nameMapper, VinWriter writer) {
        return new FilteredDelegate(
                PathFilters.fileNameStartsWith(prefix),
                new FileConverter<>(parser::parse, nameMapper, writer::write)
        );
    }
}
