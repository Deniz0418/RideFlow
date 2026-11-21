package rideflow.simulation;

/** Virtual time, measured in integer minutes. No actual waiting/sleeping. */
public final class SimulationClock {
    private int minute;
    public SimulationClock(int initialMinute) {
        if (initialMinute<0) throw new IllegalArgumentException("Invalid start time");
        minute=initialMinute;
    }
    public int now() { return minute; }
    public void tick() { minute++; }
    public String formatted() { return String.format("Day %d %02d:%02d", minute/1440+1, (minute%1440)/60, minute%60); }
}
