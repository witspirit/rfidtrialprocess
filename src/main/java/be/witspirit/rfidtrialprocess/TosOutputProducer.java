package be.witspirit.rfidtrialprocess;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Writes TOS Instructions to a destination writer
 */
public class TosOutputProducer {

    public void write(List<TosInstruction> instructions, Writer destination) {
        try {
            for (TosInstruction instruction : instructions) {
                destination.write(instruction.output() + "\n");
            }
            destination.flush();
        } catch (IOException e) {
            throw new OutputException("Failed to write instructions to destination", e);
        }
    }
}
