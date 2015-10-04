package com.taxisurfr.server;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.*;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.RouteInfo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.joda.time.DateTime.now;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class BookingServiceManager extends Manager
{
    private static final Logger logger = Logger.getLogger(BookingServiceManager.class.getName());
    static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");

    public BookingServiceManager()
    {
        ObjectifyService.register(Booking.class);
        ObjectifyService.register(Profil.class);
        ObjectifyService.register(ArchivedBooking.class);
    }

    public BookingInfo addBookingWithClient(BookingInfo bookingInfo, String client) throws IllegalArgumentException
    {
        Booking booking = Booking.getBooking(bookingInfo, client);

        booking.setRoute(bookingInfo.getRouteId());
        ofy().save().entity(booking).now();
        RouteInfo routeInfo = ofy().load().type(Route.class).id(booking.getRoute()).now().getInfo();
        return booking.getBookingInfo(routeInfo);

    }

    final Function<Booking, BookingInfo> BOOKING_TO_INFO = new Function<Booking, BookingInfo>()
    {
        @Override
        public BookingInfo apply(Booking booking)
        {

            RouteInfo routeInfo = ofy().load().type(Route.class).id(booking.getRoute()).now().getInfo();
            return booking.getBookingInfo(routeInfo);
        }
    };

    public List<BookingInfo> getBookings()
    {
        return FluentIterable.from(ofy().load().type(Booking.class).list()).transform(BOOKING_TO_INFO).toList();
    }

    public List<BookingInfo> getBookings(Long agentId) throws IllegalArgumentException
    {
        List<Booking> resultList = ofy().load().type(Booking.class).list();
        List<BookingInfo> bookings = newArrayList();
        for (Booking booking : resultList)
        {
            RouteInfo routeInfo = ofy().load().type(Route.class).id(booking.getRoute()).now().getInfo();
            if (routeInfo != null)
            {
                BookingInfo bookingInfo = booking.getBookingInfo(routeInfo);
                if (bookingInfo != null)
                {
                    Contractor contractor = ofy().load().type(Contractor.class).id(routeInfo.getContractorId()).now();

                    if (contractor != null)
                    {
                        if (contractor.getAgentId().equals(agentId))
                        {
                            bookings.add(bookingInfo);
                        }
                    }
                    else
                    {
                        logger.severe("no contractor for id:" + routeInfo.getContractorId() + " from route id:" + routeInfo.getContractorId());
                    }
                }
            }
            else
            {
                logger.severe("no route for id:" + booking.getRoute() + " from booking" + booking.getInfo().getId());
            }
        }
        return bookings;
    }


    public BookingInfo setPayed(Profil profil, BookingInfo bi, OrderStatus orderStatus, String orderRef) throws IllegalArgumentException
    {
        Booking booking = ofy().load().type(Booking.class).id(bi.getId()).now();
        booking.setStatus(orderStatus);
        booking.setRef(orderRef);
        ofy().save().entity(booking);
        RouteInfo routeInfo = ofy().load().type(Route.class).id(booking.getRoute()).now().getInfo();
        return booking.getBookingInfo(routeInfo);
    }


    public boolean getMaintenceAllowed()
    {

        boolean maintenanceAllowed = false;
        Config config = Config.getConfig();
        if (config.getMaintenceAllowed() == null)
        {
            logger.info("maintence allowed not avail - setting false");
            config.setMaintenceAllowed(false);
            ofy().save().entity(config);

        }
        else
        {
            maintenanceAllowed = config.getMaintenceAllowed();
        }
        return maintenanceAllowed;
    }

    public Profil getProfil()
    {
        Config config = new ConfigManager().getConfig();
        return ofy().load().type(Profil.class).filter("name", config.getProfil()).first().now();
    }

    public class BookingInfoComparator implements Comparator<BookingInfo>
    {

        @Override
        public int compare(BookingInfo bi1, BookingInfo bi2)
        {
            return (new DateTime(bi1.getDate()).isAfter(new DateTime(bi2.getDate()))) ? 1 : -1;
        }
    }

    @SuppressWarnings("rawtypes")
    public List<BookingInfo> getBookingsForRoute(final RouteInfo routeInfo) throws IllegalArgumentException
    {
        final Predicate<Booking> accept = new Predicate<Booking>()
        {
            @Override
            public boolean apply(Booking booking)
            {
                boolean applies = false;
                if (new DateTime(booking.getDate()).isAfter(now()) && booking.getOrderType() != null)
                {
                    boolean bookingForRoute = (booking.getRoute().equals(routeInfo.getId())) || (booking.getRoute().equals(routeInfo.getAssociatedRoute()));
                    switch (booking.getOrderType())
                    {
                        case BOOKING:
                            applies = bookingForRoute && (OrderStatus.PAID == booking.getStatus() && booking.getShareWanted());
                            break;
                        case SHARE:
                            break;
                        case SHARE_ANNOUNCEMENT:
                            applies = bookingForRoute;
                            break;
                        default:
                            break;

                    }
                }
                return applies;
            }
        };
        //

        List<Booking> bookings = newArrayList();
        List<Booking> routes = ofy().load().type(Booking.class).list();

        List<BookingInfo> listBookingInfo = newArrayList();
        List<Booking> current = from(routes).filter(accept).toList();
        for (Booking booking : current)
        {
            Route route = ofy().load().type(Route.class).id(booking.getRoute()).now();
            listBookingInfo.add(booking.getBookingInfo(route.getInfo()));
        }
        Collections.sort(listBookingInfo, new BookingInfoComparator());
        logger.info("share candidates size = " + current.size());
        return listBookingInfo;

    }

    public BookingInfo setShareAccepted(BookingInfo bookingInfo)
    {
        Booking booking = ofy().load().type(Booking.class).id(bookingInfo.getId()).now();
        booking.setStatus(OrderStatus.SHARE_ACCEPTED);
        ofy().save().entity(booking).now();
        return booking.getInfo();
    }

    public BookingInfo getBooking(Long id)
    {
        BookingInfo bookingInfo = ofy().load().type(Booking.class).id(id).now().getInfo();
        RouteInfo routeInfo = ofy().load().type(Route.class).id(bookingInfo.getRouteId()).now().getInfo();
        bookingInfo.setRouteInfo(routeInfo);
        return bookingInfo;

    }

    public List<BookingInfo> getListFeedbackRequest()
    {
        List<BookingInfo> bookings = new ArrayList<>();
        List<Booking> resultList = ofy().load().type(Booking.class).list();
        for (Booking booking : resultList)
        {
            if (booking.getRated() != null && !booking.getRated() && OrderStatus.PAID.equals(booking.getStatus()))
            {
                DateTime bookingDate = new DateTime(booking.getDate());
                if (bookingDate.plusDays(1).isBefore(DateTime.now()))
                {
                    booking.setRated(true);
                    ofy().save().entity(booking).now();

                    RouteInfo routeInfo = ofy().load().type(Route.class).id(booking.getRoute()).now().getInfo();
                    BookingInfo bookingInfo = booking.getBookingInfo(routeInfo);
                    bookings.add(bookingInfo);
                }
            }
        }
        return bookings;
    }

    public List<BookingInfo> getArchiveList()
    {
        List<Booking> resultList = ofy().load().type(Booking.class).list();
        List<BookingInfo> bookings = new ArrayList<>();
        for (Booking booking : resultList)
        {
            if (new DateTime(booking.getDate()).plusDays(7).isBefore(DateTime.now()))
            {
                RouteInfo routeInfo = ofy().load().type(Route.class).id(booking.getRoute()).now().getInfo();
                bookings.add(booking.getBookingInfo(routeInfo));
            }
        }
        return bookings;
    }

    public void archive(BookingInfo bookingInfo)
    {
        Booking booking = ofy().load().type(Booking.class).id(bookingInfo.getId()).now();
        ArchivedBooking achivedBooking = booking.getArchivedBooking();
        ofy().save().entity(achivedBooking).now();
        ofy().delete().entity(booking).now();
    }

    public void cancel(BookingInfo bookingInfo)
    {
        Booking booking = ofy().load().type(Booking.class).id(bookingInfo.getId()).now();

        booking.setStatus(OrderStatus.CANCELED);
        ofy().save().entity(booking).now();
    }

    public ContractorInfo getContractor(BookingInfo bookingInfo)
    {
        Route route = ofy().load().type(Route.class).id(bookingInfo.getRouteInfo().getId()).now();
        if (route != null)
        {
            Contractor contractor = ofy().load().type(Contractor.class).id(route.getContractorId()).now();
            return contractor.getInfo();
        }
        return null;
    }

    public AgentInfo getAgent(ContractorInfo contractorInfo)
    {
        return ofy().load().type(Agent.class).id(contractorInfo.getAgentId()).now().getInfo();
    }

    public AgentInfo createAgentWithRoutes(String agentEmail)
    {
        ConfigManager configManager = new ConfigManager();
        configManager.createTestConfig();
        BookingServiceManager bookingServiceManager = new BookingServiceManager();
        AgentManager agentManager = new AgentManager();
        RouteServiceManager routeServiceManager = new RouteServiceManager();
        ContractorManager contractorManager = new ContractorManager();

        //agent
        Agent testAgentEntity = new Agent();
        testAgentEntity.setUserEmail(agentEmail);
        ObjectifyService.ofy().save().entity(testAgentEntity).now();

        //contractor 1
        Contractor contractor1 = new Contractor();
        contractor1.setAgentId(testAgentEntity.id);
        contractor1.setName(testAgentEntity.getUserEmail() + ":contractor1");
        ObjectifyService.ofy().save().entity(contractor1).now();

        //contractor 2
        Contractor contractor2 = new Contractor();
        contractor2.setAgentId(testAgentEntity.id);
        contractor2.setName(testAgentEntity.getUserEmail() + ":contractor2");
        ObjectifyService.ofy().save().entity(contractor2).now();

        //route 1 contractor 1
        Route route11 = new Route();
        route11.setStart("colombo" + "simple_Contractor1");
        route11.setEnd("hikkawaka:" + "end");
        route11.setPickupType(RouteInfo.PickupType.AIRPORT);
        route11.setCents(101L);
        route11.setAgentCents(100L);
        route11.setContractorId(contractor1.id);
        ObjectifyService.ofy().save().entity(route11).now();

        //route 2 contractor 1
        Route route21 = new Route();
        route21.setStart("colombo" + "simple_Contractor1");
        route21.setEnd("arugam bay" + "end");
        route21.setPickupType(RouteInfo.PickupType.AIRPORT);
        route21.setCents(101L);
        route21.setAgentCents(100L);
        route21.setContractorId(contractor1.id);
        ObjectifyService.ofy().save().entity(route21).now();

        //route 1 contractor 2
        Route route12= new Route();
        route12.setStart("testAgent:" + "simple_Contractor2");
        route12.setEnd("testAgent:" + "end");
        route12.setPickupType(RouteInfo.PickupType.AIRPORT);
        route12.setCents(101L);
        route12.setAgentCents(100L);
        route12.setContractorId(contractor2.id);
        ObjectifyService.ofy().save().entity(route12).now();

        //route 2 contractor 1
        Route route22 = new Route();
        route22.setStart("testAgent:" + "simple_Contractor2");
        route22.setEnd("testAgent:" + "end");
        route22.setPickupType(RouteInfo.PickupType.AIRPORT);
        route22.setCents(101L);
        route22.setAgentCents(100L);
        route22.setContractorId(contractor2.id);
        ObjectifyService.ofy().save().entity(route22).now();

        return  testAgentEntity.getInfo();
    }

}
