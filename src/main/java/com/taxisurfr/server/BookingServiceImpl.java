package com.taxisurfr.server;

import com.google.appengine.api.users.User;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.Profil;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.model.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.taxisurfr.shared.OrderStatus.SHARE_ACCEPTED;

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
    public BookingInfo addBooking(BookingInfo bookingInfo) throws IllegalArgumentException
    {
        return bookingServiceManager.addBookingWithClient(bookingInfo, getClient());
    }

    @Override
    public List<RouteInfo> deleteRoute(AgentInfo userInfo, RouteInfo placeInfo) throws IllegalArgumentException
    {
        return routeServiceManager.deleteRoute(userInfo, placeInfo);
    }

    @Override
    public List<RouteInfo> saveRoute(AgentInfo userInfo, RouteInfo placeInfo, RouteInfo.SaveMode mode) throws IllegalArgumentException
    {
        return routeServiceManager.saveRoute(userInfo, placeInfo, mode);
    }

    @Override
    public List<RouteInfo> getRoutes(AgentInfo userInfo) throws IllegalArgumentException
    {
        return routeServiceManager.getRoutes(userInfo);
    }

    @Override
    public List<RouteInfo> getRoutes() throws IllegalArgumentException
    {
        //TODO remover
        return null;
    }

    @Override
    public List<RouteInfo> getRoutes(String query) throws IllegalArgumentException
    {
        return routeServiceManager.getRoutes(query);
    }

    @Override
    public List<BookingInfo> getBookings(AgentInfo agentInfo) throws IllegalArgumentException
    {
        return bookingServiceManager.getBookings(agentInfo.getId());
    }


    @Override
    public ProfilInfo getPaypalProfil() throws IllegalArgumentException
    {
        ProfilInfo profilInfo = bookingServiceManager.getProfil().getInfo();
        logger.info(profilInfo.toString());
        return profilInfo;

    }

    private String getClient()
    {
        return getThreadLocalRequest().getRemoteHost();
    }

    @Override
    public List<BookingInfo> getBookingsForRoute(RouteInfo routeInfo) throws IllegalArgumentException
    {
        logger.info("route inquiry:" + routeInfo.getStart() + " -> " + routeInfo.getEnd());
        return bookingServiceManager.getBookingsForRoute(routeInfo);
    }

    @Override
    public List<BookingInfo> handleShareAccepted(Long sharerId) throws IllegalArgumentException
    {
        BookingInfo sharer = bookingServiceManager.getBooking(sharerId);
        BookingInfo parentBookingInfo = bookingServiceManager.getBooking(sharer.getParentId());

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
    public BookingInfo sendShareRequest(BookingInfo bookingInfo)
    {
        Profil profil = bookingServiceManager.getProfil();
        BookingInfo parentBooking = bookingServiceManager.getBooking(bookingInfo.getParentId());
        Mailer.sendShareRequest(parentBooking, bookingInfo, profil);
        return bookingInfo;
    }

    @Override
    public BookingInfo payWithStripe(String token, BookingInfo bookingInfo)
    {
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
    public void sendStat(StatInfo statInfo)
    {
        logger.info(statInfo.getDetail());
        statManager.updateSessionStat(statInfo);
    }

    @Override
    public AgentInfo getUser() throws IllegalArgumentException
    {
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
    public List<RatingInfo> getRatings(RouteInfo routeInfo) throws IllegalArgumentException
    {
        return ratingManager.getRatings(routeInfo);
    }

    @Override
    public void addRating(RatingInfo ratingInfo) throws IllegalArgumentException
    {
        ratingManager.add(ratingInfo);
    }

    @Override
    public List<ContractorInfo> getContractors(AgentInfo agentInfo) throws IllegalArgumentException
    {
        return contractorManager.getContractors(agentInfo);
    }

    @Override
    public List<ContractorInfo> deleteContractor(AgentInfo agentInfo, ContractorInfo contractorInfo) throws IllegalArgumentException
    {
        return contractorManager.deleteContractor(agentInfo, contractorInfo);
    }

    @Override
    public List<ContractorInfo> saveContractor(AgentInfo agentInfo, ContractorInfo contractorInfo, ContractorInfo.SaveMode mode) throws IllegalArgumentException
    {
        return contractorManager.saveContractor(agentInfo, contractorInfo, mode);
    }

    @Override
    public List<AgentInfo> getAgents() throws IllegalArgumentException
    {
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
    public RouteInfo getRoute(Long routeId) throws IllegalArgumentException
    {
        return routeServiceManager.getRoute(routeId);
    }

    @Override public void resetRoutes() throws IllegalArgumentException
    {
        routeServiceManager.resetCache();
    }

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

        return getBookings(agentInfo);

    }

    @Override
    public List<FinanceInfo> savePayment(FinanceInfo financeInfo)
    {
        return financeManager.addTransfer(financeInfo);
    }

    @Override public String getMailingList()
    {
        return archivedBookingManager.getMailingList();
    }
}
