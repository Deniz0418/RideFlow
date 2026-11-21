package rideflow.model;

public final class EconomyVehicle extends Vehicle {
    public EconomyVehicle(String id, String plate) { super(id, plate); }
    @Override public VehicleCategory getCategory() { return VehicleCategory.ECONOMY; }
    @Override public int getCapacity() { return 4; }
}
