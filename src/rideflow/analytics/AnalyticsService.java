package rideflow.analytics;

import rideflow.booking.*;
import rideflow.finance.Transaction;
import rideflow.model.Driver;
import java.util.*;
/** Initial metrics. Extend with utilization and matching-strategy comparisons. */
public final class AnalyticsService {
    public String summary(List<Ride> rides, List<Driver> drivers, List<Transaction> transactions) {
        long completed=rides.stream().filter(r->r.getStatus()==RideStatus.COMPLETED).count();
        long cancelled=rides.stream().filter(r->r.getStatus()==RideStatus.CANCELLED).count();
        long waiting=rides.stream().filter(r->r.getStatus()==RideStatus.QUEUED).count();
        double revenue=transactions.stream().filter(Transaction::successful).mapToDouble(Transaction::amount).sum();
        double paidOut=drivers.stream().mapToDouble(Driver::getTotalEarnings).sum();
        double avgWait=rides.stream().filter(r->r.getStartedAtMinute()>=0)
            .mapToInt(r->r.getStartedAtMinute()-r.getRequest().getRequestedAtMinute()).average().orElse(0.0);
        return String.format(Locale.ROOT,
            "Rides: %d | Completed: %d | Cancelled: %d | Queued: %d%n"+
            "Average time to pickup: %.1f min | Revenue: $%.2f | Driver earnings: $%.2f",
            rides.size(),completed,cancelled,waiting,avgWait,revenue,paidOut);
    }
}
