package rideflow.app;

import rideflow.booking.Ride;
import rideflow.model.*;
import java.util.*;
/** Starter console only; Member 2 owns expansion into complete menus. */
public final class ConsoleMenu {
    private final AppContext app;
    private final Scanner in;
    public ConsoleMenu(AppContext app, Scanner in) { this.app=app; this.in=in; }
    public void start() {
        System.out.println("RideFlow starter | Java 21 | No Maven | " + app.simulation.timeLabel());
        while (true) {
            System.out.println("\n1 List drivers  2 Request ride  3 Advance time  4 List rides  5 Report  6 Cancel ride  0 Exit");
            System.out.print("> ");
            if (!in.hasNextLine()) return;
            String option=in.nextLine().trim();
            try {
                switch (option) {
                    case "1" -> listDrivers();
                    case "2" -> requestRide();
                    case "3" -> advanceTime();
                    case "4" -> app.rides.listRides().forEach(System.out::println);
                    case "5" -> System.out.println(app.analytics.summary(app.rides.listRides(),app.fleet.listDrivers(),app.payments.listTransactions()));
                    case "6" -> cancel();
                    case "0" -> { System.out.println("Goodbye"); return; }
                    default -> System.out.println("Unknown option");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Request could not be completed: " + e.getMessage());
            }
        }
    }
    private String ask(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) throw new IllegalStateException("No input received");
        return in.nextLine().trim();
    }
    private void listDrivers() {
        for (Driver d:app.fleet.listDrivers())
            System.out.println(d.getId()+" "+d.getName()+" "+d.getVehicle()+" "+d.getLocation()+" "+d.getStatus());
    }
    private void requestRide() {
        String passengerId=ask("Passenger ID (P001/P002/P003): ").toUpperCase(Locale.ROOT);
        int partySize=Integer.parseInt(ask("Passengers (1-6): "));
        VehicleCategory category=VehicleCategory.valueOf(ask("Category (ECONOMY/PREMIUM/XL): ").toUpperCase(Locale.ROOT));
        Location pickup=new Location(Double.parseDouble(ask("Pickup X: ")),Double.parseDouble(ask("Pickup Y: ")));
        Location destination=new Location(Double.parseDouble(ask("Destination X: ")),Double.parseDouble(ask("Destination Y: ")));
        Ride ride=app.simulation.submitRide(app.users.getPassenger(passengerId),pickup,destination,partySize,category);
        System.out.println("Created: "+ride);
    }
    private void advanceTime() {
        int minutes=Integer.parseInt(ask("Minutes to advance: "));
        app.simulation.advanceMinutes(minutes);
        System.out.println("Now: "+app.simulation.timeLabel());
    }
    private void cancel() {
        String rideId=ask("Ride ID to cancel: ").toUpperCase(Locale.ROOT);
        app.simulation.cancelRide(rideId);
        System.out.println("Cancelled: "+rideId);
    }
}
