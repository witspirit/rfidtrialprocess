package be.witspirit.rfidtrialprocess.trial.phidata;

import be.witspirit.rfidtrialprocess.rfidscan.phidata.RfidScan;
import be.witspirit.rfidtrialprocess.tos.TosInstruction;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts RfidScans to TosInstructions
 */
public class TosTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(TosTransformer.class);

    private String tosPattern;

    public TosTransformer(String tosPattern) {
        this.tosPattern = tosPattern;
    }

    public List<TosInstruction> toTos(List<RfidScan> scans) {
        List<TosInstruction> instructions = new ArrayList<>();
        for (RfidScan scan : scans) {
            byte[] uid = scan.getUid();

            try {
                String vin = parseVin(uid);

                instructions.add(new TosInstruction(tosPattern, vin));
            } catch (Exception e) {
                LOG.warn("Failed to parse "+ Hex.encodeHexString(uid)+" as a VIN. Skipping scan...", e);
            }
        }
        return instructions;

    }

    private String parseVin(byte[] bytes) {
        return new String(bytes, StandardCharsets.US_ASCII);
    }
}
