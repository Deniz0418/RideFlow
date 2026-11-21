package rideflow.finance;

import rideflow.booking.Ride;
import rideflow.booking.RideStatus;
import java.util.*;
/** Completed rides may receive one successful payment, with failed attempts logged. */
public final class PaymentService {
    private final List<Transaction> transactions = new ArrayList<>();
    private int nextTransactionNumber=1;
    public PaymentResult processPayment(Ride ride, PaymentMethod method, int minute) {
        Objects.requireNonNull(ride); Objects.requireNonNull(method);
        if (ride.getStatus()!=RideStatus.COMPLETED) throw new IllegalStateException("Cannot pay before completion");
        boolean previouslyPaid=transactions.stream().anyMatch(t->t.rideId().equals(ride.getId()) && t.successful());
        if (previouslyPaid) throw new IllegalStateException("Ride already paid");
        PaymentResult result=method.charge(ride.getQuotedFare());
        transactions.add(new Transaction(String.format("T%03d",nextTransactionNumber++),
            ride.getId(),ride.getQuotedFare(),method.name(),result.success(),minute));
        if (result.success()) ride.getDriver().creditEarnings(StandardPricing.roundMoney(ride.getQuotedFare()*0.8));
        return result;
    }
    public List<Transaction> listTransactions() { return List.copyOf(transactions); }
}
