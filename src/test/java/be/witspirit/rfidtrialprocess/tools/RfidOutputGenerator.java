package be.witspirit.rfidtrialprocess.tools;

import be.witspirit.rfidtrialprocess.InputException;
import be.witspirit.rfidtrialprocess.RfidScan;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Helper class to produce RFID Output files in the format produced by the demo application
 */
public class RfidOutputGenerator {

    // private static final Logger LOG = LoggerFactory.getLogger(RfidOutputGenerator.class);

    public static void writeTo(Writer writer, List<RfidScan> scans) {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withDelimiter(';').withHeader("No", "TrType", "UID", "Time", "AntNo").print(writer)) {

            for (RfidScan scan : scans) {
                int no = scan.getNr();
                String trType = "0x" + Integer.toHexString(scan.getTransponderType());
                String uid = Hex.encodeHexString(scan.getUid());
                String time = scan.getTimestamp().toString();
                int antNo = scan.getAntennaNr();

                printer.printRecord(no, trType, uid, time, antNo);
            }

            printer.flush();
        } catch (IOException e) {
            throw new InputException("Failed to write to output writer", e);
        }
    }
}
