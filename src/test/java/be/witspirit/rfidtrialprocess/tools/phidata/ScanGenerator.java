package be.witspirit.rfidtrialprocess.tools.phidata;

import be.witspirit.rfidtrialprocess.rfidscan.phidata.PhiDataRfidScan;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Small utility to generate a number of scan records
 */
public class ScanGenerator {

    public static List<PhiDataRfidScan> scans(String... vins) {
        List<PhiDataRfidScan> scans = new ArrayList<>();
        int counter = 1;
        for (String vin : vins) {
            PhiDataRfidScan scan = new PhiDataRfidScan();
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
