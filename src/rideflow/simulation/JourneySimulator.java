package rideflow.simulation;

import rideflow.model.Location;

/** Straight-line movement at a fixed speed; each tick has a separate travel budget. */
public final class JourneySimulator {
    private final double speedKmPerHour;
    public JourneySimulator() { this(30.0); }
    public JourneySimulator(double speedKmPerHour) {
        if (!Double.isFinite(speedKmPerHour) || speedKmPerHour <= 0 || speedKmPerHour / 60 == 0)
            throw new IllegalArgumentException("Speed must be finite and positive");
        this.speedKmPerHour = speedKmPerHour;
    }
    public double getSpeedKmPerHour() { return speedKmPerHour; }
    public int travelMinutes(Location from, Location to) {
        double distance = from.distanceTo(to);
        if (distance == 0) return 0;
        double minutes = Math.ceil(distance / (speedKmPerHour / 60));
        if (minutes > Integer.MAX_VALUE) throw new IllegalArgumentException("Journey is too long");
        return (int) Math.max(1, minutes);
    }
    public Location moveOneMinute(Location from, Location target) {
        double budget = speedKmPerHour / 60;
        double remaining = from.distanceTo(target);
        // Repeated interpolation can leave a few floating-point rounding bits.
        // Snap at that scale so a ten-minute diagonal trip does not take eleven.
        double roundingTolerance = 8 * Math.ulp(Math.max(remaining, budget));
        if (remaining <= budget + roundingTolerance) return target;
        return from.moveTowards(target, budget);
    }
}
