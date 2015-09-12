package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.file.FileConverter;
import be.witspirit.rfidtrialprocess.file.FileNameMappers;
import be.witspirit.rfidtrialprocess.file.FileProcessor;
import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidInputParser;
import be.witspirit.rfidtrialprocess.tos.TosInstruction;
import be.witspirit.rfidtrialprocess.tos.TosOutputProducer;
import be.witspirit.rfidtrialprocess.tos.TosTransformer;
import be.witspirit.rfidtrialprocess.tos.TrialInstructions;
import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

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
                        this::readPhiDataScan,
                        FileNameMappers.fixed(Paths.get(outputFileName)),
                        (vins, fileWriter) -> write(instructionPattern, vins, fileWriter)
                )).file(inputFileName);
    }

    private Stream<String> readPhiDataScan(FileReader fileReader) {
        return new PhiDataRfidInputParser().parse(fileReader);
    }

    private void write(String instructionPattern, Stream<String> vins, FileWriter fileWriter) {
        List<TosInstruction> instructions = new TosTransformer(instructionPattern).vinsToTos(vins);
        new TosOutputProducer().write(instructions, fileWriter);
    }

}
