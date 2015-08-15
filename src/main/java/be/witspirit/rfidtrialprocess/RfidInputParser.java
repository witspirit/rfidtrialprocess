package be.witspirit.rfidtrialprocess;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the Input CSV
 */
public class RfidInputParser {

    private static final Logger LOG = LoggerFactory.getLogger(RfidInputParser.class);

    public List<RfidScan> readFrom(Reader reader) {
        List<RfidScan> scans = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(reader)) {

            for (CSVRecord record : parser.getRecords()) {
                try {
                    RfidScan scan = parseRecord(record);

                    scans.add(scan);
                } catch (Exception e) {
                    LOG.warn("Failed to parse "+record+" as RFID Scan. Skipping record...", e);
                }
            }
        } catch (IOException e) {
            throw new InputException("Failed to read from input reader", e);
        }

        return scans;
    }

    private RfidScan parseRecord(CSVRecord record) {
        // Extract values
        String nr = record.get("No");
        String trType = record.get("TrType");
        String uid = record.get("UID");
        String time = record.get("Time");
        String antNr = record.get("AntNo");

        LOG.debug(nr + ":" + trType + ":" + uid + ":" + time + ":" + antNr);


        // Parse/Transform
        RfidScan scan = new RfidScan();

        scan.setNr(Integer.parseInt(nr));

        scan.setTransponderType(Integer.parseInt(trType.substring(2), 16));

        try {
            scan.setUid(Hex.decodeHex(uid.toCharArray()));
        } catch (DecoderException e) {
            throw new InputException("Failed to parse the hex value from "+uid, e);
        }

        scan.setTimestamp(LocalTime.parse(time));

        scan.setAntennaNr(Integer.parseInt(antNr));

        LOG.debug(scan.toString());

        return scan;
    }
}
