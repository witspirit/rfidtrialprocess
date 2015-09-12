package be.witspirit.rfidtrialprocess.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Small utility to produce an EPC code for a VIN, with some appropriate EPC header information
 * Format based on recommendation in Monza 4 Tag Datasheet
 */
public class EpcCodeWriter {

    private static final int HEADER_BYTE = 0x00; // Only the lowest order byte will effectively be used
    private static final int MME_PEN = 22092; // Decimal representation of the IANA Private Enterprise Number for MME (The only Mazda PEN I could find)

    public static byte[] epcBytes(String vin) {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            baos.write(HEADER_BYTE);
            baos.write(ByteBuffer.allocate(4).putInt(MME_PEN).array());
            baos.write(vin.getBytes(StandardCharsets.US_ASCII));

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to produce EPC code byte array for vin "+vin, e);
        }
    }


}
