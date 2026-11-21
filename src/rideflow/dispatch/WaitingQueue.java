package rideflow.dispatch;

import java.util.*;
/** FIFO request IDs, rather than duplicate Ride objects. */
public final class WaitingQueue {
    private final Deque<String> rideIds = new ArrayDeque<>();
    private final Set<String> seen = new HashSet<>();
    public void add(String rideId) { if (seen.add(rideId)) rideIds.addLast(rideId); }
    public void remove(String rideId) { if (seen.remove(rideId)) rideIds.remove(rideId); }
    public List<String> snapshot() { return List.copyOf(rideIds); }
    public int size() { return rideIds.size(); }
}
