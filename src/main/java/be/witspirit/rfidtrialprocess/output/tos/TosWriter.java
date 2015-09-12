package be.witspirit.rfidtrialprocess.output.tos;

import be.witspirit.rfidtrialprocess.output.VinWriter;

import java.io.Writer;
import java.util.stream.Stream;

/**
 * Combines a TosOutputProducer and a certain pattern configuration
 */
public class TosWriter implements VinWriter {

    private String tosPattern;

    public TosWriter(String tosPattern) {
        this.tosPattern = tosPattern;
    }

    @Override
    public void write(Stream<String> vins, Writer writer) {
        new TosOutputProducer().write(vins.map(TosInstruction.withPattern(tosPattern)), writer);
    }
}
