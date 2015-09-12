package be.witspirit.rfidtrialprocess.file;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * If the filter matches, it will delegate to the target consumer
 */
public class FilteredDelegate implements Consumer<Path> {
    private final Predicate<Path> filter;
    private final Consumer<Path> target;

    public FilteredDelegate(Predicate<Path> filter, Consumer<Path> target) {
        this.filter = filter;
        this.target = target;
    }

    @Override
    public void accept(Path path) {
        if (filter.test(path)) {
            target.accept(path);
        }
    }
}
