package be.witspirit.rfidtrialprocess.rfidscan.phidata;

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
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reads the Input CSV
 */
public class PhiDataRfidInputParser implements VinParser {

    private static final Logger LOG = LoggerFactory.getLogger(PhiDataRfidInputParser.class);

    public List<PhiDataRfidScan> readFrom(Reader reader) {
        List<PhiDataRfidScan> scans = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(reader)) {

            for (CSVRecord record : parser.getRecords()) {
                try {
                    PhiDataRfidScan scan = parseRecord(record);

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

    private PhiDataRfidScan parseRecord(CSVRecord record) {
        // Extract values
        String nr = record.get("No");
        String trType = record.get("TrType");
        String uid = record.get("UID");
        String time = record.get("Time");
        String antNr = record.get("AntNo");

        LOG.debug(nr + ":" + trType + ":" + uid + ":" + time + ":" + antNr);


        // Parse/Transform
        PhiDataRfidScan scan = new PhiDataRfidScan();

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

    @Override
    public Stream<String> parse(Reader reader) {
        return readFrom(reader).stream().map(this::phiDataVinExtractor);
    }

    private String phiDataVinExtractor(PhiDataRfidScan scan) {
        return new String(scan.getUid(), StandardCharsets.US_ASCII);
    }

}
