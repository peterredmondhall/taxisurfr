package com.taxisurfr.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.*;
import com.taxisurfr.server.js.NewSessionJS;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.model.StatInfo;

import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Set;
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

    public static final class Query{
        public String start;
        public String end;
    }


    @ApiMethod(name = "routes.start", httpMethod = "post")
    public Set<String> getRoutesStart(@Named("query") String query) throws IllegalArgumentException {
        logger.info("query start:"+query);
        Set<String> result = routeServiceManager.getRoutesStart(query);
        logger.info("result size:"+result.size());
        return result;
    }

    @ApiMethod(name = "routes.end", httpMethod = "post")
    public List<Route> getRoutesEnd(Query query) throws IllegalArgumentException {
        logger.info("query start:"+query.start+" end:"+query.end);
        List<Route> result = routeServiceManager.getRoutesFromQuery(query.start, query.end);
        logger.info("result size:"+result.size());
        return result;

    }

    @ApiMethod(name = "routes.query", httpMethod = "post")
    public List<Route> getRoutesByQuery(@Named("query") String query) throws IllegalArgumentException {
        logger.info("query:" + query);
        return routeServiceManager.getRoutesAsEntities(query);
    }

    @ApiMethod(name = "route.link", httpMethod = "post")
    public Route getRouteById(@Named("routeId")String routeId) throws IllegalArgumentException {
        return routeServiceManager.getRoute(Long.parseLong(routeId));
    }

    @ApiMethod(name = "session.get", httpMethod = "post")
    public StatInfo getStat(Route route) throws IllegalArgumentException {
        ObjectifyService.begin();
        StatInfo statInfo = new StatInfo();
        statInfo.setStripePublishable(bookingManager.getProfil().getStripePublishable());
        return statInfo;
    }

    @ApiMethod(name = "session.new", httpMethod = "post")
    public SessionStat addSession(NewSessionJS newSessionJS) throws IllegalArgumentException {
        logger.info("session.new"+newSessionJS.start+" to "+newSessionJS.end);
        return statManager.addRoute(newSessionJS.reference, newSessionJS.route, newSessionJS.start, newSessionJS.end);
    }

    @ApiMethod(name = "booking.new", httpMethod = "post")
    public Booking addBooking(Booking booking) throws IllegalArgumentException {
        logger.info("addBooking");
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
