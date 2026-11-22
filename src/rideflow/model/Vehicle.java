package rideflow.model;

import java.util.Locale;

/** Vehicle identity is immutable. Subclasses provide capacity and category. */
public abstract class Vehicle {
    private final String plate;

    protected Vehicle(String plate) {
        this.plate = User.requireText(plate, "Plate").toUpperCase(Locale.ROOT);
    }
    public final String getPlate() { return plate; }
    public abstract int getCapacity();
    public abstract VehicleCategory getCategory();

    public final boolean canCarry(int partySize) {
        return partySize > 0 && partySize <= getCapacity();
    }

    @Override public String toString() {
        return getCategory() + " " + plate + " (" + getCapacity() + " seats)";
    }
}
