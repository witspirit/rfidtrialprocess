package be.witspirit.rfidtrialprocess;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for ParseInput
 */
public class ParseInputTest {

    @Test
    public void readSample() throws IOException, DecoderException {
        ParseInput parser = new ParseInput();
        try (InputStream sampleStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("BRMLOG_D2015-22-06_T104026.csv")) {
            List<RfidScan> scans = parser.readFrom(sampleStream);


            assertThat(scans.size(), is(409));
        }

    }
}
