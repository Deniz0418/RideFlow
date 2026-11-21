package rideflow.finance;

/** A transaction record may describe either a successful or failed attempt. */
public record Transaction(String id, String rideId, double amount, String method,
                          boolean successful, int minute) { }
