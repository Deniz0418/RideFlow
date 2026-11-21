package rideflow.finance;

import rideflow.booking.RideRequest;
/** Placeholder demand model; improve with queued demand per zone. */
public final class SurgePricing implements PricingStrategy {
    private final StandardPricing base = new StandardPricing();
    @Override public double quote(RideRequest request, int eligibleDrivers) {
        double multiplier = eligibleDrivers <= 1 ? 1.5 : eligibleDrivers <= 3 ? 1.25 : 1.0;
        return StandardPricing.roundMoney(base.quote(request,eligibleDrivers)*multiplier);
    }
    @Override public String name() { return "Basic surge pricing"; }
}
