package rideflow.model;

/** An immutable point in the virtual city. One unit is one kilometre. */
public final class Location {
    private final double x;
    private final double y;
    public Location(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) throw new IllegalArgumentException("Invalid coordinates");
        this.x = x;
        this.y = y;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double distanceTo(Location other) {
        if (other == null) throw new IllegalArgumentException("Location cannot be null");
        return Math.hypot(x - other.x, y - other.y);
    }
    @Override public String toString() { return "(" + x + ", " + y + ")"; }
}
