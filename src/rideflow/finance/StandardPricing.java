package rideflow.finance;

import rideflow.booking.RideRequest;
import rideflow.model.VehicleCategory;
public final class StandardPricing implements PricingStrategy {
    @Override public double quote(RideRequest request, int eligibleDrivers) {
        double multiplier = switch (request.getCategory()) {
            case ECONOMY -> 1.0;
            case PREMIUM -> 1.7;
            case XL -> 1.5;
        };
        return roundMoney((3.00 + 1.80 * request.getTripDistanceKm()) * multiplier);
    }
    static double roundMoney(double amount) { return Math.round(amount * 100) / 100.0; }
    @Override public String name() { return "Standard"; }
}
