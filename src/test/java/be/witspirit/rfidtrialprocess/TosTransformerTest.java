package be.witspirit.rfidtrialprocess;

import be.witspirit.rfidtrialprocess.tools.ScanGenerator;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit test for TosTransformer
 */
public class TosTransformerTest {

    @Test
    public void sample() {
        List<RfidScan> scans = scans("VIN01234567890V01", "VIN01234567890V02", "VIN01234567890V03");

        TosTransformer tosTransformer = new TosTransformer("Dummy Instruction, vin %s, some more, data");
        List<TosInstruction> instructions = tosTransformer.toTos(scans);

        int counter = 1;
        for (TosInstruction instruction : instructions) {
            assertThat(instruction.output(), is("Dummy Instruction, vin VIN01234567890V0"+(counter++)+", some more, data"));
        }
    }

    @Test
    public void arrival() {
        List<TosInstruction> instructions = new TosTransformer(TrialInstructions.ARRIVAL).toTos(scans("VIN01234567890V01"));

        assertThat(instructions.size(), is(1));

        TosInstruction instruction = instructions.get(0);
        assertThat(instruction.output(), is("PositionScan, vin VIN01234567890V01, position AF1001, slot 1"));
    }

    @Test
    public void vpcDone() {
        List<TosInstruction> instructions = new TosTransformer(TrialInstructions.VPC_DONE).toTos(scans("VIN01234567890V01"));

        assertThat(instructions.size(), is(1));

        TosInstruction instruction = instructions.get(0);
        assertThat(instruction.output(), is("PDI_Done, vin VIN01234567890V01"));
    }

    @Test
    public void departure() {
        List<TosInstruction> instructions = new TosTransformer(TrialInstructions.DEPARTURE).toTos(scans("VIN01234567890V01"));

        assertThat(instructions.size(), is(1));

        TosInstruction instruction = instructions.get(0);
        assertThat(instruction.output(), is("LoadScan, vin VIN01234567890V01, visit STOBART_ZBRRFIDTEST"));
    }



    private List<RfidScan> scans(String... vins) {
        return ScanGenerator.scans(vins);
    }
}
