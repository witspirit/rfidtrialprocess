package be.witspirit.rfidtrialprocess;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the Input CSV
 */
public class ParseInput {

    private static final Logger LOG = LoggerFactory.getLogger(ParseInput.class);

    public List<RfidScan> readFrom(InputStream inputStream) throws IOException {
        List<RfidScan> scans = new ArrayList<>();
        InputStreamReader input = new InputStreamReader(inputStream);
        CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(input);
        for (CSVRecord record : parser.getRecords()) {
            String nr = record.get("No");
            String trType = record.get("TrType");
            String uid = record.get("UID");
            String time = record.get("Time");
            String antNr = record.get("AntNo");

            LOG.debug(nr+":"+trType+":"+uid+":"+time+":"+antNr);
        }

        return scans;
    }
}
