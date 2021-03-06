package be.witspirit.rfidtrialprocess.file;

import be.witspirit.rfidtrialprocess.exceptions.InputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Takes a file path as input and produces a new file from it
 */
public class FileConverter<ParsedType> implements Consumer<Path> {
    private static final Logger LOG = LoggerFactory.getLogger(FileConverter.class);

    private Function<FileReader, ParsedType> fileParser;
    private Function<Path, Path> fileNameMapper;
    private BiConsumer<ParsedType, FileWriter> outputProducer;

    private List<BiConsumer<Path, Path>> postProcessors = new ArrayList<>();

    public FileConverter(Function<FileReader, ParsedType> fileParser, Function<Path, Path> fileNameMapper, BiConsumer<ParsedType, FileWriter> outputProducer) {
        this.fileParser = fileParser;
        this.fileNameMapper = fileNameMapper;
        this.outputProducer = outputProducer;
    }

    public FileConverter<ParsedType> addPostProcessor(BiConsumer<Path, Path>... postProcessors) {
        this.postProcessors.addAll(Arrays.asList(postProcessors));
        return this;
    }

    @Override
    public void accept(Path inputFilePath) {
        Path outputFilePath = fileNameMapper.apply(inputFilePath);
        try (
                FileReader inputReader = openReader(inputFilePath);
                FileWriter fileWriter = new FileWriter(outputFilePath.toFile())
        ){

            ParsedType parsedResult = fileParser.apply(inputReader);
            outputProducer.accept(parsedResult, fileWriter);

            postProcessors.forEach(pp -> runPostProcessor(pp, inputFilePath, outputFilePath));

        } catch (IOException e) {
            throw new RuntimeException("I/O issues prevented completion of the transformation of "+inputFilePath, e);
        }
    }

    // Ensure the process does not fail because one of the post processors didn't run succesfully
    private void runPostProcessor(BiConsumer<Path, Path> postProcessor, Path inputFilePath, Path outputFilePath) {
        try {
            postProcessor.accept(inputFilePath, outputFilePath);
        } catch (Exception e) {
            LOG.error("Failed to execute post processor "+postProcessor, e);
        }
    }

    private FileReader openReader(Path inputFilePath) {
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
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new InputException("Failed to read "+inputFilePath, e);
        }
    }
}
