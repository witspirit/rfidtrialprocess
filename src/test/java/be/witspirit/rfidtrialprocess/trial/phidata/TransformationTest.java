package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.file.FileConverter;
import be.witspirit.rfidtrialprocess.file.FileNameMappers;
import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.output.tos.TosWriter;
import be.witspirit.rfidtrialprocess.output.tos.TrialInstructions;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Small integration test that will convert the sample RFID scan csv to various TosInstruction formats
 */
public class TransformationTest {
    private static final String INPUT_DIR = "src/test/resources/phidata";
    private static final String INPUT_FILE = "BRMLOG_D2015-22-06_T104026.csv";

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
        new FileProcessor(Paths.get(INPUT_DIR))
                .register(new FileConverter<>(
                        new PhiDataRfidInputParser()::parse,
                        FileNameMappers.fixed(Paths.get(outputFileName)),
                        new TosWriter(instructionPattern)::write
                )).file(inputFileName);
    }

}
