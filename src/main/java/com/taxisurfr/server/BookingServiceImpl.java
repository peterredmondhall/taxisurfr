package com.taxisurfr.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

import com.google.appengine.api.users.User;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.Profil;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.model.*;

import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.taxisurfr.shared.OrderStatus.SHARE_ACCEPTED;

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
public class BookingServiceImpl extends RemoteServiceServlet implements
        BookingService
{
    private static final Logger logger = Logger.getLogger(BookingServiceImpl.class.getName());

    private final BookingServiceManager bookingServiceManager = new BookingServiceManager();
    private final ArchivedBookingManager archivedBookingManager = new ArchivedBookingManager();
    private final RouteServiceManager routeServiceManager = new RouteServiceManager();
    private final AgentManager agentManager = new AgentManager();
    private final ContractorManager contractorManager = new ContractorManager();
    private final StatManager statManager = new StatManager();
    private final RatingManager ratingManager = new RatingManager();
    private final FinanceManager financeManager = new FinanceManager();
    private final CurrencyManager currencyManager = new CurrencyManager();
    private final StripePayment stripePayment = new StripePayment();

    @Override
    //@ApiMethod(name = "bookings.add", httpMethod = "post")
    public BookingInfo addBooking(BookingInfo bookingInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return bookingServiceManager.addBookingWithClient(bookingInfo, getClient());
    }

    @Override
    //@ApiMethod(name = "route.delete", httpMethod = "post")
    public List<RouteInfo> deleteRoute(AgentInfo userInfo, RouteInfo placeInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return routeServiceManager.deleteRoute(userInfo, placeInfo);
    }

    @Override
    public List<RouteInfo> saveRoute(AgentInfo userInfo, RouteInfo placeInfo, @Named("mode")RouteInfo.SaveMode mode) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return routeServiceManager.saveRoute(userInfo, placeInfo, mode);
    }

    @Override
    public List<RouteInfo> getRoutesByAgent(AgentInfo userInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return routeServiceManager.getRoutes(userInfo);
    }

    @Override
    @ApiMethod(name = "routes.get.query", httpMethod = "post")
    public List<RouteInfo> getRoutesByQuery(@Named("query")String query) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return routeServiceManager.getRoutes(query);
    }

    @Override
    @ApiMethod(name = "bookings.agent", path = "bookingsagent",httpMethod = "post")
    public List<BookingInfo> getBookingsForAgent(AgentInfo agentInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return bookingServiceManager.getBookingsAsInfo(agentInfo.getId());
    }


    @Override
    //@ApiMethod(name = "profil.get", httpMethod = "post")
    public ProfilInfo getPaypalProfil() throws IllegalArgumentException
    {
        ObjectifyService.begin();
        ProfilInfo profilInfo = bookingServiceManager.getProfil().getInfo();
        logger.info(profilInfo.toString());
        return profilInfo;

    }

    private String getClient()
    {
        return getThreadLocalRequest().getRemoteHost();
    }

    @Override
    @ApiMethod(name = "bookings.route", path = "bookingsroute",httpMethod = "post")
    public List<BookingInfo> getBookingsForRoute(RouteInfo routeInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        logger.info("route inquiry:" + routeInfo.getStart() + " -> " + routeInfo.getEnd());
        return bookingServiceManager.getBookingsForRoute(routeInfo);
    }

    @Override
    @ApiMethod(name = "bookings.share.accepted", httpMethod = "post")
    public List<BookingInfo> handleShareAccepted(@Named("id")Long sharerId) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        BookingInfo sharer = bookingServiceManager.getBookingAsInfo(sharerId);
        BookingInfo parentBookingInfo = bookingServiceManager.getBookingAsInfo(sharer.getParentId());

        BookingInfo sharerBookingInfo = bookingServiceManager.setShareAccepted(sharer);
        List<BookingInfo> list = null;
        if (sharerBookingInfo.getStatus() == SHARE_ACCEPTED)
        {
            list = Lists.newArrayList(parentBookingInfo, sharerBookingInfo);
            Mailer.sendShareAccepted(sharerBookingInfo.getEmail(), parentBookingInfo, sharer, bookingServiceManager.getProfil());
        }
        else
        {
            logger.severe("share failed " + sharerId);
        }
        return list;
    }

    @Override
    //@ApiMethod(name = "share.request.send", httpMethod = "post")
    public BookingInfo sendShareRequest(BookingInfo bookingInfo)
    {
        ObjectifyService.begin();
        Profil profil = bookingServiceManager.getProfil();
        BookingInfo parentBooking = bookingServiceManager.getBookingAsInfo(bookingInfo.getParentId());
        Mailer.sendShareRequest(parentBooking, bookingInfo, profil);
        return bookingInfo;
    }

    @Override
    public BookingInfo payWithStripe(@Named("token")String token, BookingInfo bookingInfo)
    {
        ObjectifyService.begin();
        logger.info("payWithStripe" + bookingInfo.getPaidPrice());
        Profil profil = bookingServiceManager.getProfil();
        logger.info("payWithStripe" + bookingInfo.getPaidPrice());
        Route route = ofy().load().type(Route.class).id(bookingInfo.getRouteId()).now();
        ContractorInfo contractorInfo = bookingServiceManager.getContractor(bookingInfo);
        Agent agent = ofy().load().type(Agent.class).id(contractorInfo.getAgentId()).now();
        Long orderCount = agent.getOrderCount();
        String orderRef = orderCount+"_"+bookingInfo.getName();

        String refusal = stripePayment.charge(token, bookingInfo, profil.getStripeSecret(),orderRef);
        if (refusal == null)
        {
            bookingInfo = bookingServiceManager.setPayed(profil, bookingInfo, OrderStatus.PAID,orderRef);
            if (bookingInfo != null)
            {
                AgentInfo agentInfo = bookingServiceManager.getAgent(contractorInfo);
                Mailer.sendConfirmation(bookingInfo, profil, agentInfo, contractorInfo);
                financeManager.addPayment(bookingInfo, new Date());
                ofy().save().entity(agent).now();
            }
        }
        else
        {
            bookingInfo.setStripeRefusalReason(refusal);
        }
        return bookingInfo;
    }


    @Override
    //@ApiMethod(name = "stat.send", httpMethod = "post")
    public void sendStat(StatInfo statInfo)
    {
        ObjectifyService.begin();
        logger.info(statInfo.getDetail());
        statManager.updateSessionStat(statInfo);
    }

    @Override
    //@ApiMethod(name = "agent.get", httpMethod = "post")
    public AgentInfo getUser() throws IllegalArgumentException
    {
        ObjectifyService.begin();
        AgentInfo userInfo = null;
        User user = getUserFromSession();
        if (user != null)
        {
            userInfo = agentManager.getAgent(user.getEmail());
        }
        return userInfo;
    }

    private User getUserFromSession()
    {
        Object obj = getThreadLocalRequest().getSession().getAttribute("user");
        if (obj == null)
        {
            return null;
        }
        return (User) obj;
    }

    @Override
    //@ApiMethod(name = "ratings.get", httpMethod = "post")
    public List<RatingInfo> getRatings(RouteInfo routeInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return ratingManager.getRatings(routeInfo);
    }

    @Override
    //@ApiMethod(name = "ratings.add", httpMethod = "post")
    public void addRating(RatingInfo ratingInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        ratingManager.add(ratingInfo);
    }

    @Override
    //@ApiMethod(name = "contractors.get", httpMethod = "post")
    public List<ContractorInfo> getContractors(AgentInfo agentInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return contractorManager.getContractors(agentInfo);
    }

    @Override
    //@ApiMethod(name = "contractor.delete", httpMethod = "post")
    public List<ContractorInfo> deleteContractor(AgentInfo agentInfo, ContractorInfo contractorInfo) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return contractorManager.deleteContractor(agentInfo, contractorInfo);
    }

    @Override
    //@ApiMethod(name = "contractor.save", httpMethod = "post")
    public List<ContractorInfo> saveContractor(AgentInfo agentInfo, ContractorInfo contractorInfo, @Named("mode")ContractorInfo.SaveMode mode) throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return contractorManager.saveContractor(agentInfo, contractorInfo, mode);
    }

    @Override
    //@ApiMethod(name = "agents.get", httpMethod = "post")
    public List<AgentInfo> getAgents() throws IllegalArgumentException
    {
        ObjectifyService.begin();
        return agentManager.getAgents();
    }

    public void sendRatingRequest()
    {
        Profil profil = bookingServiceManager.getProfil();
        List<BookingInfo> list = bookingServiceManager.getListFeedbackRequest();
        logger.info("todo size:" + list.size());
        for (BookingInfo bi : list)
        {
            Mailer.setFeedbackRequest(bi, profil);
        }

        for (BookingInfo bi : bookingServiceManager.getArchiveList())
        {
            bookingServiceManager.archive(bi);
        }

    }

    @Override
    public RouteInfo getRoute(@Named("id")Long routeId) throws IllegalArgumentException
    {
        return routeServiceManager.getRoute(routeId).getInfo();
    }

    //@ApiMethod(name = "routes.reset", httpMethod = "post")
    @Override public void resetRoutes() throws IllegalArgumentException
    {
        routeServiceManager.resetCache();
    }

    //@ApiMethod(name = "routes.test.init", httpMethod = "post")
    @Override public void initTestRoutes() throws IllegalArgumentException
    {
        bookingServiceManager.createAgentWithRoutes("test@example.com");
    }

    @Override
    public List<FinanceInfo> getFinances(AgentInfo agentInfo)
    {
        return financeManager.getFinance(agentInfo);
    }

    @Override
    public BookingInfo getCurrencyRate(BookingInfo bookingInfo) throws IllegalArgumentException
    {
        logger.info("getCurrencyRate " + bookingInfo.getCurrency());
        Float rate = currencyManager.getRate(bookingInfo.getCurrency());
        if (rate == null)
        {
            logger.info("getCurrencyRate is null, defaulting to USD ");
            bookingInfo.setCurrency(Currency.USD);
        }
        bookingInfo.setRate(rate);
        logger.info("getCurrencyRate " + bookingInfo.getRate());
        return bookingInfo;
    }

    @Override
    public List<BookingInfo> cancelBooking(BookingInfo bookingInfo, AgentInfo agentInfo) throws IllegalArgumentException
    {
        bookingServiceManager.cancel(bookingInfo);
        financeManager.cancel(bookingInfo.getId());

        return getBookingsForAgent(agentInfo);

    }

    @Override
    //@ApiMethod(name = "payment.save", httpMethod = "post")
    public List<FinanceInfo> savePayment(FinanceInfo financeInfo)
    {
        return financeManager.addTransfer(financeInfo);
    }


    @Override public String getMailingList()
    {
        return archivedBookingManager.getMailingList();
    }
}
