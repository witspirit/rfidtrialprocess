package be.witspirit.rfidtrialprocess.output.tos;

import java.util.function.Function;

/**
 * Representation of a TOS Instruction
 */
public class TosInstruction {
    private String pattern;
    private String vin;

    public TosInstruction(String pattern, String vin) {
        this.pattern = pattern;
        this.vin = vin;
    }

    public String output() {
        return String.format(pattern, vin);
    }

    @Override
    public String toString() {
        return output();
    }

    public static Function<String, TosInstruction> withPattern(String pattern) {
        return vin -> new TosInstruction(pattern, vin);
    }
}
