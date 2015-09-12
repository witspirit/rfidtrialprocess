package be.witspirit.rfidtrialprocess.output.tos;

import be.witspirit.rfidtrialprocess.exceptions.OutputException;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes TOS Instructions to a destination writer.
 * Since the TOS application is a Windows application, we assume Windows Line Separators.
 */
public class TosOutputProducer {

    public static final String WINDOWS_LINE_SEPARATOR = "\r\n";

    public void write(Stream<TosInstruction> instructions, Writer destination) {
        write(instructions.collect(Collectors.toList()), destination);
    }

    // Yes, primary implementation via List to avoid a bunch of ugly try/catch blocks for IOException
    // With streams, I have to do this on multiple levels :-(
    public void write(List<TosInstruction> instructions, Writer destination) {
        try {
            for (TosInstruction instruction : instructions) {
                destination.write(instruction.output() + WINDOWS_LINE_SEPARATOR);
            }
            destination.flush();
        } catch (IOException e) {
            throw new OutputException("Failed to write instructions to destination", e);
        }
    }
}
