package be.witspirit.rfidtrialprocess;

import be.witspirit.rfidtrialprocess.tools.EpcCodeWriter;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

/**
 * Can be used to easily create some clean EPC Codes for a VIN
 */
public class EpcCodeWriterTest {

    @Test
    public void vinToEpcCode() {
        System.out.println(Hex.encodeHex(EpcCodeWriter.epcBytes("JMZCR19F260100442")));
    }
}
