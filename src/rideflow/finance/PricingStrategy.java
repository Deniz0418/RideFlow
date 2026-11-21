package rideflow.finance;

import rideflow.booking.RideRequest;
/** Member 4 contract: strategy owns quoted fare calculation. */
public interface PricingStrategy {
    double quote(RideRequest request, int eligibleDriverCount);
    String name();
}
