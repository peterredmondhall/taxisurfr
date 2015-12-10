package com.taxisurfr.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.taxisurfr.server.entity.*;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.model.StatInfo;

import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Api(
        name = "taxisurfr",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE}
)

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TaxisurfrFacade {
    private static final Logger logger = Logger.getLogger(TaxisurfrFacade.class.getName());

    private final RouteServiceManager routeServiceManager = new RouteServiceManager();
    private final StatManager statManager = new StatManager();
    private final BookingServiceManager bookingManager = new BookingServiceManager();
    private final StripePayment stripePayment = new StripePayment();
    private final FinanceManager financeManager = new FinanceManager();
    private final ContractorManager contractorManager = new ContractorManager();
    private final AgentManager agentManager = new AgentManager();




    @ApiMethod(name = "routes.query", httpMethod = "post")
    public List<Route> getRoutesByQuery(@Named("query") String query) throws IllegalArgumentException {
        System.out.println("query:" + query);
        return routeServiceManager.getRoutesAsEntities(query);
    }

    @ApiMethod(name = "route.link", httpMethod = "post")
    public Route getRouteById(@Named("routeId")String routeId) throws IllegalArgumentException {
        return routeServiceManager.getRoute(Long.parseLong(routeId));
    }

    @ApiMethod(name = "session.get", httpMethod = "post")
    public StatInfo getStat(Route route) throws IllegalArgumentException {
        StatInfo statInfo = new StatInfo();
        statInfo.setStripePublishable(bookingManager.getProfil().getStripePublishable());
        return statInfo;
    }

    @ApiMethod(name = "session.new", httpMethod = "post")
    public SessionStat addSession(Route route) throws IllegalArgumentException {
        System.out.println("addSession");
        SessionStat sessionStat = new SessionStat();
        sessionStat.setRoute(route.getStart() + " to " + route.getEnd());
        return statManager.createSessionStat(sessionStat);
    }

    @ApiMethod(name = "booking.new", httpMethod = "post")
    public Booking addBooking(Booking booking) throws IllegalArgumentException {
        System.out.println("addBooking");
        return bookingManager.createBooking(booking);
    }

    @ApiMethod(name = "booking.pay", httpMethod = "post")
    public Booking payBooking(SessionStat sessionStat) {
        Booking booking = bookingManager.getBooking(sessionStat.getBookingId());
        Profil profil = bookingManager.getProfil();
        Route route = ofy().load().type(Route.class).id(booking.getRoute()).now();
        booking.setPaidPrice(route.getCents().intValue() / 100);
        Contractor contractor = contractorManager.getContractor(route);
        Agent agent = agentManager.getAgent(contractor);
        Long orderCount = agent.getOrderCount();
        booking.setRef(orderCount + "_" + booking.getName());

        String refusal = stripePayment.charge(sessionStat.getCardToken(), booking, profil.getStripeSecret());
        if (refusal == null) {
            booking = bookingManager.setPayed(profil, booking);
            if (booking != null) {
                Mailer.sendConfirmation(booking, route, profil, agent, contractor);
                financeManager.addPayment(booking, new Date());
                ofy().save().entity(agent).now();
            }
        } else {
            booking.setStripeRefusal(refusal);
        }
        return booking;
    }

}
