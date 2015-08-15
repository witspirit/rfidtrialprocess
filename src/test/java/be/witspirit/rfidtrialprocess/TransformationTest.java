package be.witspirit.rfidtrialprocess;

import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * Small integration test that will convert the sample RFID scan csv to various TosInstruction formats
 */
public class TransformationTest {
    private static final String INPUT_FILE = "src/test/resources/BRMLOG_D2015-22-06_T104026.csv";

    private static final String ARRIVAL_FILE = "build/arrivals.txt";
    private static final String VPC_DONE_FILE = "build/washes.txt";
    private static final String DEPARTURE_FILE = "build/departures.txt";

    @Test
    public void arrivals() {
        process(INPUT_FILE, TrialInstructions.ARRIVAL, ARRIVAL_FILE);
    }

    @Test
    public void washes() {
        process(INPUT_FILE, TrialInstructions.VPC_DONE, VPC_DONE_FILE);
    }

    @Test
    public void departures() {
        process(INPUT_FILE, TrialInstructions.DEPARTURE, DEPARTURE_FILE);
    }

    private void process(String inputFileName, String instructionPattern, String outputFileName) {
        try {
            List<RfidScan> scans = new RfidInputParser().readFrom(new FileReader(inputFileName));
            List<TosInstruction> instructions = new TosTransformer(instructionPattern).toTos(scans);
            new TosOutputProducer().write(instructions, new FileWriter(outputFileName));
        } catch (Exception e) {
            throw new RuntimeException("Conversion failed", e);
        }
    }


}
