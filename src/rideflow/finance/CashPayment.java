package rideflow.finance;

/** Simulated cash confirmation. No real payment is made. */
public final class CashPayment implements PaymentMethod {
    @Override public String name() { return "CASH"; }
    @Override public PaymentResult charge(double amount) { return new PaymentResult(true, "Cash accepted (simulated)"); }
}
