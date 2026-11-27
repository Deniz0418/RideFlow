package rideflow.dispatch;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public final class WaitingQueue {

    private final Deque<String> rideIds = new ArrayDeque<>();

    public void add(String id) {
        if (id != null && !id.isBlank() && !rideIds.contains(id)) {
            rideIds.addLast(id);
        }
    }

    public void remove(String id) {
        if (id != null) {
            rideIds.remove(id);
        }
    }

    public List<String> snapshot() {
        return List.copyOf(rideIds);
    }

    public boolean contains(String id) {
        return id != null && rideIds.contains(id);
    }

    public int size() {
        return rideIds.size();
    }

    public boolean isEmpty() {
        return rideIds.isEmpty();
    }
}
