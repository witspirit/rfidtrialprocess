package be.witspirit.rfidtrialprocess.rfidscan.phidata;

import be.witspirit.rfidtrialprocess.tools.phidata.RfidOutputGenerator;
import be.witspirit.rfidtrialprocess.tools.phidata.ScanGenerator;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;

/**
 * Not really a test, but a helper to produce some sample RFID scan files
 */
public class RfidOutputGeneratorTest {

    @Test
    public void mazdaLikeSample() throws IOException {
        // Some VINs selected from DSR Checklists to have some real-life VINs
        List<PhiDataRfidScan> sampleScans = ScanGenerator.scans(
                "JMZCR19F260100442",
                "JMZBK14Z201587911",
                "JMZDE13K200105949",
                "JMZCR198260109131",
                "JMZBLA4Z201248741",
                "JMZGJ621821127661",
                "SDKCR19F200100060",
                "JMZBM546611129835",
                "JMZCWA98600101904",
                "JMZCR198260109135",
                "JMZNB18P600220137",
                "JMZBLB4Y611549617",
                "JMZBLB2F711568634",
                "JMZBLB2F711568761",
                "JMZCR19F200100063",
                "JMZGY19R671457352",
                "JMZCR19R680221425",
                "JMZCR198200103336",
                "JMZCR19R660109181",
                "JMZCR19R660109217",
                "JMZERH9A600248997",
                "JMZCR19F200100058",
                "JMZDW192200149944",
                "JMZCR19F260100441",
                "JMZCR198260107496",
                "ZZZGJ597621103082",
                "JMZCR19R600112066",
                "JMZNCA8F600257319",
                "JMZDW192200103035",
                "JMZGF12F201171392",
                "JMZDE14K200144658",
                "JMZBK14Z281668534",
                "JMZDW193200103803",
                "JMZGF12F201168744",
                "JMZCR19F200100066",
                "JMZBM427831127967",
                "JMZCR19F260113806",
                "JMZDW193200100561",
                "JMZNC2WPJN6J51842",
                "JMZBL12Z501145534",
                "JMZGJ597611103629",
                "JMZNC18F600204819",
                "JMZNCA88200351077",
                "JMZBL14Z201180238",
                "JMZGHA48201455329",
                "JMZLW19R230201773",
                "JMZGG148231143467",
                "JMZBK12Z251239424",
                "JMZKEF91600121222",
                "JMZCR198260119861"
        );

        try (Writer fileWriter = new FileWriter(Paths.get("build", "mazdaLikeSample.csv").toFile())) {
            RfidOutputGenerator.writeTo(fileWriter, sampleScans);
        }
    }

}
