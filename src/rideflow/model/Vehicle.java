package rideflow.model;

/** A vehicle belongs to a driver; category and capacity cannot change mid-trip. */
public abstract class Vehicle {
    private final String id;
    private final String plate;
    protected Vehicle(String id, String plate) {
        if (id == null || id.isBlank() || plate == null || plate.isBlank()) throw new IllegalArgumentException("Vehicle ID and plate are required");
        this.id = id;
        this.plate = plate;
    }
    public final String getId() { return id; }
    public final String getPlate() { return plate; }
    public abstract VehicleCategory getCategory();
    public abstract int getCapacity();
    @Override public String toString() { return getCategory() + " [" + plate + ", " + getCapacity() + " seats]"; }
}
