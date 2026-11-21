package rideflow.finance;

import rideflow.booking.RideRequest;
import java.util.Objects;
public final class PricingService {
    private PricingStrategy strategy;
    public PricingService(PricingStrategy strategy) { this.strategy=Objects.requireNonNull(strategy); }
    public void setStrategy(PricingStrategy strategy) { this.strategy=Objects.requireNonNull(strategy); }
    public double quote(RideRequest request, int eligibleDrivers) { return strategy.quote(request,eligibleDrivers); }
    public String strategyName() { return strategy.name(); }
}
