package be.witspirit.rfidtrialprocess;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Writes TOS Instructions to a destination writer.
 * Since the TOS application is a Windows application, we assume Windows Line Separators.
 */
public class TosOutputProducer {

    public static final String WINDOWS_LINE_SEPARATOR = "\r\n";

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
