package rideflow.finance;

public interface PaymentMethod {
    String name();
    PaymentResult charge(double amount);
}
