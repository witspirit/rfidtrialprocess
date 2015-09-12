package be.witspirit.rfidtrialprocess.rfidscan;

import java.io.Reader;
import java.util.stream.Stream;

/**
 * Abstraction for a parser that delivers a stream of VINs from a reader.
 */
public interface VinParser {

    Stream<String> parse(Reader reader);
}
