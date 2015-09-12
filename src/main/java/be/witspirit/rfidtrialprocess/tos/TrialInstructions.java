package be.witspirit.rfidtrialprocess.tos;

/**
 * Defines the TOS Patterns for the various Trial scenario's we have identified
 */
public interface TrialInstructions {

    String ARRIVAL = "PositionScan, vin %s, position AF1001, slot 1";
    String VPC_DONE = "PDI_Done, vin %s";
    String DEPARTURE = "LoadScan, vin %s, visit STOBART_ZBRRFIDTEST";
}
