package be.witspirit.rfidtrialprocess;

import java.time.Instant;

/**
 * Represents an individual RFID Scan line
 */
public class RfidScan {

    private int nr;

    private int transponderType;

    private byte[] uid;

    private Instant timestamp;

    private int antennaNr;

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public int getTransponderType() {
        return transponderType;
    }

    public void setTransponderType(int transponderType) {
        this.transponderType = transponderType;
    }

    public byte[] getUid() {
        return uid;
    }

    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getAntennaNr() {
        return antennaNr;
    }

    public void setAntennaNr(int antennaNr) {
        this.antennaNr = antennaNr;
    }
}
