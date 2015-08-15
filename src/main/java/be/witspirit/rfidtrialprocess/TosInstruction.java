package be.witspirit.rfidtrialprocess;

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
}
