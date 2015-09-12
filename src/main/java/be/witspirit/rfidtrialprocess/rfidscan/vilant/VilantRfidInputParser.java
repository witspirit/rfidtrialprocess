package be.witspirit.rfidtrialprocess.rfidscan.vilant;

import be.witspirit.rfidtrialprocess.exceptions.InputException;
import be.witspirit.rfidtrialprocess.rfidscan.VinParser;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reads the Input CSV
 */
public class VilantRfidInputParser implements VinParser {

    private static final Logger LOG = LoggerFactory.getLogger(VilantRfidInputParser.class);

    public List<VilantRfidScan> readFrom(Reader reader) {
        List<VilantRfidScan> scans = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(reader)) {

            for (CSVRecord record : parser.getRecords()) {
                try {
                    VilantRfidScan scan = parseRecord(record);

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

    private VilantRfidScan parseRecord(CSVRecord record) {
        // Extract values
        String vin = record.get("VIN");
        String epcData = record.get("EPC");
        String timestamp = record.get("TIMESTAMP");

        LOG.debug(vin + ":" + epcData + ":" + timestamp);


        try {
            // Parse/Transform
            VilantRfidScan scan = new VilantRfidScan()
                    .setVin(vin)
                    .setEpcData(Hex.decodeHex(epcData.toCharArray()))
                    .setTimestamp(LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));

            LOG.debug(scan.toString());

            return scan;
        } catch (DecoderException e) {
            throw new InputException("Failed to decode hex representation of the EPC Data", e);
        }
    }

    @Override
    public Stream<String> parse(Reader reader) {
        return readFrom(reader).stream().map(scan -> scan.getVin());
    }
}
