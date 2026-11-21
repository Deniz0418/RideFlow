package rideflow.model;

public abstract class User {
    private final String id;
    private final String name;
    protected User(String id, String name) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) throw new IllegalArgumentException("ID and name are required");
        this.id = id;
        this.name = name;
    }
    public final String getId() { return id; }
    public final String getName() { return name; }
}
