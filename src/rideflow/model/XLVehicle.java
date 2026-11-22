package rideflow.model;

public final class XLVehicle extends Vehicle {
    public XLVehicle(String plate) { super(plate); }
    @Override public int getCapacity() { return 6; }
    @Override public VehicleCategory getCategory() { return VehicleCategory.XL; }
}
