package rideflow.simulation;

import rideflow.booking.Ride;
import rideflow.model.Location;
/** Fixed straight-line speed; Member 1 can add progressive movement later. */
public final class JourneySimulator {
    private final double speedKmPerHour;
    public JourneySimulator(double speedKmPerHour) {
        if (!Double.isFinite(speedKmPerHour) || speedKmPerHour <= 0) throw new IllegalArgumentException("Speed must be positive");
        this.speedKmPerHour=speedKmPerHour;
    }
    public int travelMinutes(Location from, Location to) {
        return Math.max(1,(int)Math.ceil(from.distanceTo(to) * 60 / speedKmPerHour));
    }
    public void arriveAtPickup(Ride ride) { ride.getDriver().setLocation(ride.getRequest().getPickup()); }
    public void arriveAtDestination(Ride ride) { ride.getDriver().setLocation(ride.getRequest().getDestination()); }
}
