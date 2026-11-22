package rideflow.model;

/** A passenger account. Ride history and active-ride rules belong to RideService. */
public final class Passenger extends User {
    public Passenger(String id, String name) { this(id, name, ""); }
    public Passenger(String id, String name, String phone) { super(id, name, phone); }
}
