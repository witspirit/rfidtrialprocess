package be.witspirit.rfidtrialprocess;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the Input CSV
 */
public class ParseInput {

    private static final Logger LOG = LoggerFactory.getLogger(ParseInput.class);

    public List<RfidScan> readFrom(InputStream inputStream) throws IOException, DecoderException {
        List<RfidScan> scans = new ArrayList<>();
        InputStreamReader input = new InputStreamReader(inputStream);
        CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(input);

        DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        for (CSVRecord record : parser.getRecords()) {
            String nr = record.get("No");
            String trType = record.get("TrType");
            String uid = record.get("UID");
            String time = record.get("Time");
            String antNr = record.get("AntNo");

            LOG.debug(nr + ":" + trType + ":" + uid + ":" + time + ":" + antNr);

            RfidScan scan = new RfidScan();
            scan.setNr(Integer.parseInt(nr));
            scan.setTransponderType(Integer.parseInt(trType.substring(2), 16));
            scan.setUid(Hex.decodeHex(uid.toCharArray()));
            scan.setTimestamp(LocalTime.parse(time));
            scan.setAntennaNr(Integer.parseInt(antNr));

            LOG.debug(scan.toString());


            scans.add(scan);

        }

        return scans;
    }
}
