package rideflow.finance;

import java.util.Map;
import rideflow.booking.Ride;

/** MEMBER 4 STUB: signatures only; no working implementation.  */
public final class PaymentService {
    public PaymentResult processPayment(Ride ride, PaymentMethod method, int minute) {
        // TODO Member 4: Require COMPLETED, deduplicate by ride ID, invoke the payment method, record transaction/outcome, and credit revenue/earnings only once after success. Be idempotent when the engine retries exceptions.
        throw new UnsupportedOperationException("Member 4: implement processPayment");
    }

    public Map<String, PaymentResult> getResults() {
        // TODO Member 4: Return an unmodifiable snapshot of payment results keyed by ride ID.
        throw new UnsupportedOperationException("Member 4: implement getResults");
    }
}
