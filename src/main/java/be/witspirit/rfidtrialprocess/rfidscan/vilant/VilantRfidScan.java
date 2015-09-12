package be.witspirit.rfidtrialprocess.rfidscan.vilant;

import org.apache.commons.codec.binary.Hex;

import java.time.LocalDateTime;

/**
 * Represents an individual RFID Scan line
 */
public class VilantRfidScan {

    private String vin;

    private byte[] epcData;

    private LocalDateTime timestamp;


    public String getVin() {
        return vin;
    }

    public VilantRfidScan setVin(String vin) {
        this.vin = vin;
        return this;
    }

    public byte[] getEpcData() {
        return epcData;
    }

    public VilantRfidScan setEpcData(byte[] epcData) {
        this.epcData = epcData;
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public VilantRfidScan setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public String toString() {
        return "Scan " + vin + ":" + Hex.encodeHexString(epcData) + ":" + timestamp;
    }
}
