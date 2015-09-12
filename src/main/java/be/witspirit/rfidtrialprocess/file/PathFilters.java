package be.witspirit.rfidtrialprocess.file;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Helpers for common Path filters
 */
public class PathFilters {

    public static Predicate<Path> fileNameStartsWith(String prefix) {
        return path -> path.getFileName().toString().startsWith(prefix);
    }
}
