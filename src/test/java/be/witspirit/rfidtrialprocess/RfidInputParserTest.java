package be.witspirit.rfidtrialprocess;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for ParseInput
 */
public class RfidInputParserTest {

    @Test
    public void readSample() throws IOException {
        RfidInputParser parser = new RfidInputParser();
        try (
                InputStream sampleStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("BRMLOG_D2015-22-06_T104026.csv");
                Reader sampleReader = new InputStreamReader(sampleStream);
        ) {
            List<RfidScan> scans = parser.readFrom(sampleReader);


            assertThat(scans.size(), is(409));

            // Check the first and last sample

            // First = 25;0x84;303030303030313338363834;10:40:45.900;1;
            RfidScan firstScan = scans.get(0);

            assertThat(firstScan.getNr(), is(25));
            assertThat(firstScan.getTransponderType(), is(0x84));
            assertThat(firstScan.getUid(), is(new byte[] {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x31, 0x33, 0x38, 0x36, 0x38, 0x34}));
            assertThat(firstScan.getTimestamp(), is(LocalTime.of(10, 40, 45, 900*1000000)));
            assertThat(firstScan.getAntennaNr(), is(1));

            // Last = 433;0x84;E2002067560F00980240F0E4;10:41:05.050;1;
            RfidScan lastScan = scans.get(scans.size()-1);

            assertThat(lastScan.getNr(), is(433));
            assertThat(lastScan.getTransponderType(), is(0x84));
            assertThat(lastScan.getUid(), is(new byte[] {(byte) 0xE2, 0x00, 0x20, 0x67, 0x56, 0x0F, 0x00, (byte) 0x98, 0x02, 0x40, (byte) 0xF0, (byte) 0xE4}));
            assertThat(lastScan.getTimestamp(), is(LocalTime.of(10, 41, 5, 50*1000000)));
            assertThat(lastScan.getAntennaNr(), is(1));

        }

    }
}
