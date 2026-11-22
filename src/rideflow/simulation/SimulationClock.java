package rideflow.simulation;

import java.util.Locale;

/** Minute zero is 10:00 on day zero. No sleeps or real-world clock are used. */
public final class SimulationClock {
    private int minute;
    public int now() { return minute; }
    public void tick() { minute = Math.addExact(minute, 1); }
    public String timeLabel() {
        long absolute = 10L * 60 + minute;
        long day = absolute / (24 * 60);
        return (day == 0 ? "" : "Day " + day + " ")
                + String.format(Locale.ROOT, "%02d:%02d", absolute / 60 % 24, absolute % 60);
    }
}
