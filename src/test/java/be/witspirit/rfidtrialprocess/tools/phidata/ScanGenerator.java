package be.witspirit.rfidtrialprocess.tools.phidata;

import be.witspirit.rfidtrialprocess.rfidscan.phidata.RfidScan;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Small utility to generate a number of scan records
 */
public class ScanGenerator {

    public static List<RfidScan> scans(String... vins) {
        List<RfidScan> scans = new ArrayList<>();
        int counter = 1;
        for (String vin : vins) {
            RfidScan scan = new RfidScan();
            scan.setNr(counter++);
            scan.setTransponderType(132);
            scan.setUid(vin.getBytes(StandardCharsets.US_ASCII));
            scan.setTimestamp(LocalTime.now());
            scan.setAntennaNr(1);

            scans.add(scan);
        }

        return scans;
    }
}
