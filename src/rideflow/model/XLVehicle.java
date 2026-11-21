package rideflow.model;

public final class XLVehicle extends Vehicle {
    public XLVehicle(String id, String plate) { super(id, plate); }
    @Override public VehicleCategory getCategory() { return VehicleCategory.XL; }
    @Override public int getCapacity() { return 6; }
}
