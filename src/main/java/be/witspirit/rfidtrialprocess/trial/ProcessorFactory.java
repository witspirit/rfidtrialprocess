package be.witspirit.rfidtrialprocess.trial;

import be.witspirit.rfidtrialprocess.file.FileConverter;
import be.witspirit.rfidtrialprocess.file.FileNameMappers;
import be.witspirit.rfidtrialprocess.output.tos.TosWriter;
import be.witspirit.rfidtrialprocess.rfidscan.VinParser;
import be.witspirit.rfidtrialprocess.rfidscan.vilant.VilantRfidInputParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Small factory to easily construct multiple file converters with the same config
 */
public class ProcessorFactory {
    private VinParser parser = new VilantRfidInputParser();
    private Function<Path, Path> nameMapper = FileNameMappers.suffix("_INSTRUCTIONS.csv");
    private List<BiConsumer<Path, Path>> postProcessors = new ArrayList<>();

    public VinParser getParser() {
        return parser;
    }

    public ProcessorFactory setParser(VinParser parser) {
        this.parser = parser;
        return this;
    }

    public Function<Path, Path> getNameMapper() {
        return nameMapper;
    }

    public ProcessorFactory setNameMapper(Function<Path, Path> nameMapper) {
        this.nameMapper = nameMapper;
        return this;
    }

    public List<BiConsumer<Path, Path>> getPostProcessors() {
        return postProcessors;
    }

    public ProcessorFactory addPostProcessors(BiConsumer<Path, Path>... postProcessors) {
        this.postProcessors.addAll(Arrays.asList(postProcessors));
        return this;
    }

    public FileConverter<Stream<String>> forIntruction(String tosPattern) {
        return new FileConverter<>(parser::parse, nameMapper, new TosWriter(tosPattern)::write).addPostProcessor(postProcessors.toArray(new BiConsumer[postProcessors.size()]));
    }
}
