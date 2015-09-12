package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidScan;
import be.witspirit.rfidtrialprocess.tos.TosInstruction;
import be.witspirit.rfidtrialprocess.tos.TosOutputProducer;
import be.witspirit.rfidtrialprocess.tos.TrialInstructions;
import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * Small integration test that will convert the sample RFID scan csv to various TosInstruction formats
 */
public class TransformationTest {
    private static final String INPUT_FILE = "src/test/resources/phidata/BRMLOG_D2015-22-06_T104026.csv";

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
            List<PhiDataRfidScan> scans = new PhiDataRfidInputParser().readFrom(new FileReader(inputFileName));
            List<TosInstruction> instructions = new TosTransformer(instructionPattern).toTos(scans);
            new TosOutputProducer().write(instructions, new FileWriter(outputFileName));
        } catch (Exception e) {
            throw new RuntimeException("Conversion failed", e);
        }
    }


}
