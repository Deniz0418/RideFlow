package rideflow.model;

public final class PremiumVehicle extends Vehicle {
    public PremiumVehicle(String id, String plate) { super(id, plate); }
    @Override public VehicleCategory getCategory() { return VehicleCategory.PREMIUM; }
    @Override public int getCapacity() { return 4; }
}
