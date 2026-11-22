package rideflow.fleet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import rideflow.model.Driver;
import rideflow.model.Passenger;
import rideflow.model.User;

/** One account directory; IDs are unique across both kinds of user. */
public final class UserDirectory {
    private final Map<String, User> users = new LinkedHashMap<>();

    public void registerPassenger(Passenger passenger) { register(passenger); }
    public void registerDriver(Driver driver) { register(driver); }

    private void register(User user) {
        Objects.requireNonNull(user, "User");
        if (users.putIfAbsent(user.getId(), user) != null)
            throw new IllegalArgumentException("Duplicate user ID: " + user.getId());
    }
    public Optional<User> findUser(String id) { return Optional.ofNullable(users.get(id)); }
    public Passenger getPassenger(String id) {
        if (users.get(id) instanceof Passenger passenger) return passenger;
        throw new IllegalArgumentException("Unknown passenger: " + id);
    }
    public Driver getDriver(String id) {
        if (users.get(id) instanceof Driver driver) return driver;
        throw new IllegalArgumentException("Unknown driver: " + id);
    }
    public List<Passenger> listPassengers() {
        return users.values().stream().filter(Passenger.class::isInstance).map(Passenger.class::cast).toList();
    }
    public List<Driver> listDrivers() {
        return users.values().stream().filter(Driver.class::isInstance).map(Driver.class::cast).toList();
    }
}
