package be.witspirit.rfidtrialprocess.file;

import be.witspirit.rfidtrialprocess.exceptions.OutputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.BiConsumer;

/**
 * Moves input files to destination directory.
 */
public class FileMover implements BiConsumer<Path, Path> {
    private static final Logger LOG = LoggerFactory.getLogger(FileMover.class);

    private Path destinationDir;

    public FileMover(Path destinationDir) {
        this.destinationDir = destinationDir;
    }

    @Override
    public void accept(Path input, Path output) {
        Path destinationFile = destinationDir.resolve(input.getFileName());

        try {
            Files.move(input, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("Moved "+input+" to "+destinationFile);
        } catch (IOException e) {
            throw new OutputException("Failed to move "+input+" to "+destinationDir, e);
        }
    }
}
