package be.witspirit.rfidtrialprocess.output;

import java.io.Writer;
import java.util.stream.Stream;

/**
 * Abstraction for writing VINs to a file
 */
public interface VinWriter {

    void write(Stream<String> vins, Writer writer);
}
