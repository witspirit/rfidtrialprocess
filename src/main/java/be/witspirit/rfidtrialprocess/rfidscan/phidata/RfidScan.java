package be.witspirit.rfidtrialprocess.rfidscan.phidata;

import org.apache.commons.codec.binary.Hex;

import java.time.LocalTime;

/**
 * Represents an individual RFID Scan line
 */
public class RfidScan {

    private int nr;

    private int transponderType;

    private byte[] uid;

    private LocalTime timestamp;

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

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getAntennaNr() {
        return antennaNr;
    }

    public void setAntennaNr(int antennaNr) {
        this.antennaNr = antennaNr;
    }

    @Override
    public String toString() {
        return "Scan "+nr+":"+transponderType+":"+ Hex.encodeHexString(uid)+":"+timestamp+":"+antennaNr;
    }
}
