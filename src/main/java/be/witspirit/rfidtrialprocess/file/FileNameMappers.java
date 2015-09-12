package be.witspirit.rfidtrialprocess.file;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Bundles some common File Name Mappers
 */
public class FileNameMappers {

    public static Function<Path, Path> fixed(Path filePath) {
        return inputPath -> filePath;
    }

    public static Function<Path, Path> toDir(Path dir) {
        return inputPath -> dir.resolve(inputPath.getFileName());
    }

    public static Function<Path, Path> suffix(String suffix) {
        return inputPath -> inputPath.getParent().resolve(inputPath.getFileName().toString()+suffix);
    }

}
