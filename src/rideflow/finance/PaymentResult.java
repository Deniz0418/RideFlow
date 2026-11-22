package rideflow.finance;

/**
 * MEMBER 4: data contract only. Java generates the constructor and accessors.
 * TODO Member 4: produce this result from each payment implementation. A failure
 * must never count as successful revenue. No charging or accounting exists here.
 */
public record PaymentResult(boolean success, String message) { }
