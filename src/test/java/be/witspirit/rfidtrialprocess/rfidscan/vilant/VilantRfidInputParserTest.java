package be.witspirit.rfidtrialprocess.rfidscan.vilant;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for ParseInput
 */
public class VilantRfidInputParserTest {

    @Test
    public void readSample() throws IOException {
        VilantRfidInputParser parser = new VilantRfidInputParser();
        try (
                InputStream sampleStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("vilant/Vilant20150912_091444.csv");
                Reader sampleReader = new InputStreamReader(sampleStream);
        ) {
            List<VilantRfidScan> scans = parser.readFrom(sampleReader);


            assertThat(scans.size(), is(10));

            // Check the first and last sample

            // First = 12345678901234567;DA07313233343536373839303132333435363700;2015.09.12 09:14:35
            VilantRfidScan firstScan = scans.get(0);

            assertThat(firstScan.getVin(), is("12345678901234567"));
            assertThat(firstScan.getEpcData(), is(bytes(0xDA, 0x07, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x00)));
            assertThat(firstScan.getTimestamp(), is(LocalDateTime.of(2015, 9, 12, 9, 14, 35)));

            // Last = 12345678901234567;DA07313233343536373839303132333435363700;2015.09.12 09:14:42
            VilantRfidScan lastScan = scans.get(scans.size()-1);

            assertThat(lastScan.getVin(), is("12345678901234567"));
            assertThat(lastScan.getEpcData(), is(bytes(0xDA, 0x07, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x00)));
            assertThat(lastScan.getTimestamp(), is(LocalDateTime.of(2015, 9, 12, 9, 14, 42)));
        }

    }

    private static byte[] bytes(int... bytes) {
        byte[] byteArray = new byte[bytes.length];
        for (int i=0; i < bytes.length; i++) {
            byteArray[i] = (byte) bytes[i];
        }
        return byteArray;
    }
}
