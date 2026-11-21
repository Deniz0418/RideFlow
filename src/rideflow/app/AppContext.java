package rideflow.app;

import rideflow.analytics.AnalyticsService;
import rideflow.booking.RideService;
import rideflow.dispatch.*;
import rideflow.finance.*;
import rideflow.fleet.*;
import rideflow.model.*;
import rideflow.simulation.*;
/** Composition root: assemble services once, share through constructors. */
public final class AppContext {
    public final UserDirectory users=new UserDirectory();
    public final FleetManager fleet=new FleetManager();
    public final RideService rides=new RideService();
    public final PricingService pricing=new PricingService(new StandardPricing());
    public final PaymentService payments=new PaymentService();
    public final DispatchService dispatch=new DispatchService(fleet,rides,pricing,new NearestDriverStrategy());
    public final SimulationClock clock=new SimulationClock(9*60);
    public final SimulationEngine simulation=new SimulationEngine(clock,rides,fleet,dispatch,payments,new JourneySimulator(60));
    public final AnalyticsService analytics=new AnalyticsService();
    public static AppContext withDemoData() {
        AppContext app=new AppContext();
        app.users.registerPassenger(new Passenger("P001","Alex"));
        app.users.registerPassenger(new Passenger("P002","Taylor"));
        app.users.registerPassenger(new Passenger("P003","Sam"));
        app.fleet.registerDriver(new Driver("D001","Morgan",new EconomyVehicle("V001","ABC-101"),new Location(0,0)));
        app.fleet.registerDriver(new Driver("D002","Riley",new EconomyVehicle("V002","ABC-102"),new Location(12,3)));
        app.fleet.registerDriver(new Driver("D003","Jordan",new XLVehicle("V003","ABC-103"),new Location(4,8)));
        app.fleet.registerDriver(new Driver("D004","Casey",new PremiumVehicle("V004","ABC-104"),new Location(9,11)));
        return app;
    }
}
