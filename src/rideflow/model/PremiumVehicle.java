package rideflow.model;

public final class PremiumVehicle extends Vehicle {
    public PremiumVehicle(String plate) { super(plate); }
    @Override public int getCapacity() { return 4; }
    @Override public VehicleCategory getCategory() { return VehicleCategory.PREMIUM; }
}
