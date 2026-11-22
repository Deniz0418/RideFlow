package rideflow.dispatch;

import java.util.List;

/** MEMBER 3 STUB: signatures only; no working implementation.  */
public final class WaitingQueue {
    public void add(String id) {
        // TODO Member 3: Append a waiting ride ID once, preserving FIFO order and avoiding duplicates.
    }

    public void remove(String id) {
        // TODO Member 3: Remove a ride ID if present, including cancellations and assignments.
    }

    public List<String> snapshot() {
        // TODO Member 3: Return an unmodifiable snapshot of waiting IDs in FIFO order.
        throw new UnsupportedOperationException("Member 3: implement snapshot");
    }
}
