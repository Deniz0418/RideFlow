package rideflow.model;

/** Common account details inherited by passengers and drivers. */
public abstract class User {
    private final String id;
    private String name;
    private String phone;

    protected User(String id, String name, String phone) {
        this.id = requireText(id, "User ID");
        setName(name);
        setPhone(phone);
    }

    public final String getId() { return id; }
    public final String getName() { return name; }
    public final String getPhone() { return phone; }
    public final void setName(String name) { this.name = requireText(name, "Name"); }
    public final void setPhone(String phone) {
        if (phone == null) throw new IllegalArgumentException("Phone cannot be null");
        this.phone = phone.trim(); // Optional in this console simulation.
    }

    static String requireText(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " cannot be blank");
        return value.trim();
    }

    @Override public String toString() { return id + " | " + name; }
}
