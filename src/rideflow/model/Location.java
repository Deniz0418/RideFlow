package rideflow.model;

import java.util.Locale;
import java.util.Objects;

/** Immutable coordinates in kilometres; negative coordinates are valid. */
public final class Location {
    private final double x;
    private final double y;

    public Location(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y))
            throw new IllegalArgumentException("Coordinates must be finite");
        this.x = x == 0 ? 0 : x;
        this.y = y == 0 ? 0 : y;
    }
    public double getX() { return x; }
    public double getY() { return y; }

    public double distanceTo(Location other) {
        Objects.requireNonNull(other, "Location");
        double distance = Math.hypot(other.x - x, other.y - y);
        if (!Double.isFinite(distance))
            throw new IllegalArgumentException("Locations are too far apart");
        return distance;
    }

    /** Move at most distanceKm toward target, without overshooting it. */
    public Location moveTowards(Location target, double distanceKm) {
        if (!Double.isFinite(distanceKm) || distanceKm < 0)
            throw new IllegalArgumentException("Movement distance must be finite and nonnegative");
        double remaining = distanceTo(target);
        if (remaining == 0 || distanceKm >= remaining) return target;
        double fraction = distanceKm / remaining;
        return new Location(x + (target.x - x) * fraction, y + (target.y - y) * fraction);
    }

    @Override public boolean equals(Object other) {
        return other instanceof Location p && Double.compare(x, p.x) == 0
                && Double.compare(y, p.y) == 0;
    }
    @Override public int hashCode() { return Objects.hash(x, y); }
    @Override public String toString() { return String.format(Locale.ROOT, "(%.2f, %.2f)", x, y); }
}
