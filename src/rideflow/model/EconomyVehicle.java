package rideflow.model;

public final class EconomyVehicle extends Vehicle {
    public EconomyVehicle(String plate) { super(plate); }
    @Override public int getCapacity() { return 4; }
    @Override public VehicleCategory getCategory() { return VehicleCategory.ECONOMY; }
}
