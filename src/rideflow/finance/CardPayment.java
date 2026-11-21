package rideflow.finance;

/** Simulated approval switch; does not connect to a card processor. */
public final class CardPayment implements PaymentMethod {
    private final boolean approved;
    public CardPayment(boolean approved) { this.approved=approved; }
    @Override public String name() { return "CARD"; }
    @Override public PaymentResult charge(double amount) { return new PaymentResult(approved, approved?"Card approved (simulated)":"Card declined (simulated)"); }
}
