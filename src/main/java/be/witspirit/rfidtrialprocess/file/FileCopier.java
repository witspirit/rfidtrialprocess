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
 * Copies output files to destination directory.
 */
public class FileCopier implements BiConsumer<Path, Path> {
    private static final Logger LOG = LoggerFactory.getLogger(FileCopier.class);

    private Path destinationDir;

    public FileCopier(Path destinationDir) {
        this.destinationDir = destinationDir;
    }

    @Override
    public void accept(Path input, Path output) {
        Path destinationFile = destinationDir.resolve(output.getFileName());

        try {
            Files.copy(output, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("Copied "+output+" to "+destinationFile);
        } catch (IOException e) {
            throw new OutputException("Failed to copy "+output+" to "+destinationDir, e);
        }
    }
}
