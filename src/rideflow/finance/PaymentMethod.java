package rideflow.finance;

/** MEMBER 4: interface declaration only; cash/card/wallet classes supply behaviour. */
@FunctionalInterface
public interface PaymentMethod {
    // TODO Member 4: implement charge in each payment type; return a truthful simulated outcome.
    PaymentResult charge(double amount);
}
