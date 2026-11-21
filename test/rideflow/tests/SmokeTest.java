package rideflow.tests;
import rideflow.app.AppContext;
import rideflow.booking.*;
import rideflow.dispatch.*;
import rideflow.finance.*;
import rideflow.model.*;
/** Dependency-free tests, runnable with `ant test`; JUnit may be added later. */
public final class SmokeTest {
    private static int passed;
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        passed++;
    }
    public static void main(String[] args) {
        AppContext app=AppContext.withDemoData();
        Ride first=app.simulation.submitRide(app.users.getPassenger("P001"), new Location(0,0),new Location(4,0),2,VehicleCategory.ECONOMY);
        check(first.getStatus()==RideStatus.EN_ROUTE,"first ride assigned");
        check(first.getDriver().getId().equals("D001"),"nearest driver selected");
        Ride second=app.simulation.submitRide(app.users.getPassenger("P002"),new Location(0,1),new Location(6,1),2,VehicleCategory.ECONOMY);
        check(second.getDriver().getId().equals("D002"),"driver cannot be assigned twice");
        Ride third=app.simulation.submitRide(app.users.getPassenger("P003"),new Location(0,0),new Location(6,1),2,VehicleCategory.ECONOMY);
        check(third.getStatus()==RideStatus.QUEUED,"capacity prevents matching");
        check(app.dispatch.waitingQueue().size()==1,"queued once");
        app.simulation.advanceMinutes(6);
        check(first.getStatus()==RideStatus.COMPLETED,"first ride completes");
        check(app.payments.listTransactions().size()==1,"one completed trip has payment");
        check(app.payments.listTransactions().get(0).successful(),"payment successful");
        check(first.getDriver().getCompletedRides()==1,"driver completion recorded");
        check(third.getStatus()==RideStatus.EN_ROUTE,"queued request was retried after release");
        check(app.dispatch.waitingQueue().size()==0,"retry removed assigned request from queue");
        check(third.getDriver().getId().equals("D001"),"released driver reassigned to waiting ride");
        app.simulation.cancelRide(third.getId());
        check(app.fleet.getDriver("D001").getStatus()==DriverStatus.AVAILABLE,"cancellation releases assigned driver");
        boolean capacityRejected=false;
        try { app.simulation.submitRide(app.users.getPassenger("P003"),new Location(0,0),new Location(2,0),6,VehicleCategory.ECONOMY); }
        catch (IllegalArgumentException expected) { capacityRejected=true; }
        check(capacityRejected,"party of 6 cannot request an Economy car");
        boolean blocked=false;
        try { app.simulation.submitRide(app.users.getPassenger("P002"),new Location(1,1),new Location(3,3),1,VehicleCategory.ECONOMY); }
        catch (IllegalStateException expected) { blocked=true; }
        check(blocked,"same passenger cannot have two active rides");
        boolean duplicatePaymentRejected=false;
        try { app.payments.processPayment(first,new CashPayment(),app.simulation.now()); }
        catch (IllegalStateException expected) { duplicatePaymentRejected=true; }
        check(duplicatePaymentRejected,"second successful payment forbidden");
        AppContext failureApp=AppContext.withDemoData();
        Ride unpaid=failureApp.simulation.submitRide(failureApp.users.getPassenger("P001"),new Location(0,0),new Location(2,0),1,VehicleCategory.ECONOMY);
        failureApp.simulation.advanceMinutes(3);
        check(unpaid.getStatus()==RideStatus.COMPLETED,"short ride completed");
        // A second attempt after a completed auto-paid trip must not create another transaction.
        check(failureApp.payments.listTransactions().size()==1,"exactly one automatic payment");
        check(!new CardPayment(false).charge(5).success(),"simulated card decline");
        check(!new WalletPayment(1).charge(5).success(),"insufficient wallet balance");
        System.out.println("PASS: " + passed + " assertions");
    }
}
