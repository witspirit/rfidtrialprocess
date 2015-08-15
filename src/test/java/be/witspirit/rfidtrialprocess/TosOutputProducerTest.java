package be.witspirit.rfidtrialprocess;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for TosOutputProducer
 */
public class TosOutputProducerTest {

    @Test
    public void sampleOutputToFile() throws IOException {
        List<TosInstruction> instructions = new ArrayList<>();
        instructions.add(new TosInstruction("Instruction 1, %s", "DummyVin1"));
        instructions.add(new TosInstruction("Instruction 2, %s", "DummyVin2"));
        instructions.add(new TosInstruction("Instruction 3, %s", "DummyVin3"));


        FileWriter destination = new FileWriter("build/sample.txt");

        TosOutputProducer producer = new TosOutputProducer();
        producer.write(instructions, destination);

        boolean filesEqual = FileUtils.contentEquals(new File("build/sample.txt"), new File("src/test/resources/expectedOutputSample.txt"));
        assertThat("File contents are not equal", filesEqual);
    }

    @Test
    public void trialOutputToFile() throws IOException {
        List<TosInstruction> instructions = new ArrayList<>();
        instructions.add(new TosInstruction(TrialInstructions.ARRIVAL, "VIN01234567890V01"));
        instructions.add(new TosInstruction(TrialInstructions.VPC_DONE, "VIN01234567890V01"));
        instructions.add(new TosInstruction(TrialInstructions.DEPARTURE, "VIN01234567890V01"));

        FileWriter destination = new FileWriter("build/trialInstructions.txt");

        TosOutputProducer producer = new TosOutputProducer();
        producer.write(instructions, destination);

        boolean filesEqual = FileUtils.contentEquals(new File("build/trialInstructions.txt"), new File("src/test/resources/expectedTrialInstructions.txt"));
        assertThat("File contents are not equal", filesEqual);
    }

}
