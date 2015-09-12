package be.witspirit.rfidtrialprocess.tos;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit test for TosTransformer
 */
public class TosTransformerTest {

    @Test
    public void sampleFromVins() {
        List<String> vins = vins("VIN01234567890V01", "VIN01234567890V02", "VIN01234567890V03");

        TosTransformer tosTransformer = new TosTransformer("Dummy Instruction, vin %s, some more, data");
        List<TosInstruction> instructions = tosTransformer.vinsToTos(vins);

        int counter = 1;
        for (TosInstruction instruction : instructions) {
            assertThat(instruction.output(), is("Dummy Instruction, vin VIN01234567890V0"+(counter++)+", some more, data"));
        }
    }

    @Test
    public void sampleFromScans() {
        List<String> vins = vins("VIN01234567890V01", "VIN01234567890V02", "VIN01234567890V03");
        List<TestScan> scans = vins.stream().map(vin -> new TestScan(vin)).collect(Collectors.toList());

        TosTransformer tosTransformer = new TosTransformer("Dummy Instruction, vin %s, some more, data");
        List<TosInstruction> instructions = tosTransformer.scansToTos(scans, scan -> scan.getVin());

        int counter = 1;
        for (TosInstruction instruction : instructions) {
            assertThat(instruction.output(), is("Dummy Instruction, vin VIN01234567890V0"+(counter++)+", some more, data"));
        }
    }

    @Test
    public void arrival() {
        List<TosInstruction> instructions = new TosTransformer(TrialInstructions.ARRIVAL).vinsToTos(vins("VIN01234567890V01"));

        assertThat(instructions.size(), is(1));

        TosInstruction instruction = instructions.get(0);
        assertThat(instruction.output(), is("PositionScan, vin VIN01234567890V01, position AF1001, slot 1"));
    }

    @Test
    public void vpcDone() {
        List<TosInstruction> instructions = new TosTransformer(TrialInstructions.VPC_DONE).vinsToTos(vins("VIN01234567890V01"));

        assertThat(instructions.size(), is(1));

        TosInstruction instruction = instructions.get(0);
        assertThat(instruction.output(), is("PDI_Done, vin VIN01234567890V01"));
    }

    @Test
    public void departure() {
        List<TosInstruction> instructions = new TosTransformer(TrialInstructions.DEPARTURE).vinsToTos(vins("VIN01234567890V01"));

        assertThat(instructions.size(), is(1));

        TosInstruction instruction = instructions.get(0);
        assertThat(instruction.output(), is("LoadScan, vin VIN01234567890V01, visit STOBART_ZBRRFIDTEST"));
    }


    private List<String> vins(String... vins) {
        return Arrays.asList(vins);
    }

    // Just a dummy Scan class to check our extractor stuff works
    private static class TestScan {
        private String vin;

        public TestScan(String vin) {
            this.vin = vin;
        }

        public String getVin() {
            return vin;
        }
    }

}
