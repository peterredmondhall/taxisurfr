package com.taxisurfr.server;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.joda.time.DateTime.now;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.*;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.OrderType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Predicate;
import com.google.common.base.Function;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.ProfilInfo;
import com.taxisurfr.shared.model.RouteInfo;

import javax.annotation.Nullable;

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
        ObjectifyService.register(Config.class);
        ObjectifyService.register(Profil.class);
        ObjectifyService.register(Agent.class);
        ObjectifyService.register(Contractor.class);
        ObjectifyService.register(Route.class);
        ObjectifyService.register(Rating.class);

    }

    public BookingInfo addBookingWithClient(BookingInfo bookingInfo, String client) throws IllegalArgumentException
    {
        Booking booking = Booking.getBooking(bookingInfo, client);
        booking.setRoute(bookingInfo.getRouteId());
        ofy().save().entity(booking).now();
        RouteInfo routeInfo = ofy().load().type(Route.class).id(booking.getRoute()).now().getInfo();
        return booking.getBookingInfo(routeInfo);

        //        logger.info(bookingInfo.toString());
        //        EntityManager em = getEntityManager();
        //        try
        //        {
        //            Route route = em.find(Route.class, bookingInfo.getRouteInfo().getId());
        //
        //            Booking booking = Booking.getBooking(bookingInfo, client);
        //            em.getTransaction().begin();
        //            em.persist(booking);
        //            em.getTransaction().commit();
        //            em.detach(booking);
        //
        //            bookingInfo = booking.getBookingInfo(route.getInfo());
        //
        //            booking = em.find(Booking.class, bookingInfo.getId());
        //            booking.setRef(booking.generateRef());
        //            em.getTransaction().begin();
        //            em.persist(booking);
        //            em.getTransaction().commit();
        //            em.detach(booking);
        //
        //            bookingInfo = booking.getBookingInfo(route.getInfo());
        //
        //        }
        //        catch (Exception e)
        //        {
        //            e.printStackTrace();
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
        //        try
        //        {
        //            if (bookingInfo.getOrderType().equals(OrderType.SHARE_ANNOUNCEMENT))
        //            {
        //                Mailer.sendShareAnnouncement(bookingInfo, getProfil());
        //            }
        //        }
        //        catch (Exception ex)
        //        {
        //            ex.printStackTrace();
        //        }
        //        return bookingInfo;
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

    public BookingInfo setBookingRef(BookingInfo bookingInfo)
    {
        Booking booking = ofy().load().type(Booking.class).id(bookingInfo.getId()).now();
        bookingInfo.setOrderRef(booking.getRef());
        return bookingInfo;

        //        Booking booking = getEntityManager().find(Booking.class, bookingInfo.getId());
        //        bookingInfo.setOrderRef(booking.getRef());
        //        return bookingInfo;
    }

    public BookingInfo setPayed(Profil profil, BookingInfo bi, OrderStatus orderStatus) throws IllegalArgumentException
    {
        Booking booking = ofy().load().type(Booking.class).id(bi.getId()).now();
        booking.setStatus(orderStatus);
        ofy().save().entity(booking);
        return booking.getInfo();

        //        EntityManager em = getEntityManager();
        //        BookingInfo bookingInfo = null;
        //        try
        //        {
        //            Booking booking = em.find(Booking.class, bi.getId());
        //
        //            booking.setStatus(orderStatus);
        //            em.getTransaction().begin();
        //            em.persist(booking);
        //            em.getTransaction().commit();
        //
        //            em.detach(booking);
        //            bookingInfo = booking.getBookingInfo(getRouteInfo(booking.getRoute(), em));
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
        //        return bookingInfo;
    }

    public ProfilInfo getPaypalProfil() throws IllegalArgumentException
    {
        throw new RuntimeException();

        //        ProfilInfo profilInfo = getProfil().getInfo();
        //        logger.info(profilInfo.toString());
        //        return profilInfo;

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

        Profil profil = null;
        Config config = null;
        List<Config> configList = ofy().load().type(Config.class).list();

        if (configList.size() == 0)
        {
            config = new Config();
            config.setProfil("test");
            config.setMaintenceAllowed(true);
            ofy().save().entity(config);
        }
        else
        {
            config = configList.get(0);
        }
        logger.info("Using config profil:" + config.getProfil());
        List<Profil> profilList = ofy().load().type(Profil.class).filter("name", config.getProfil()).list();

        if (profilList.size() == 0)
        {
            profil = new Profil();
            profil.setPaypalAccount(PaypalPaymentChecker.TEST_ACCT);
            profil.setPaypalAT(PaypalPaymentChecker.TEST_AT);
            profil.setPaypalURL(PaypalPaymentChecker.TEST_PAYPAL_URL);
            profil.setTest(true);
            profil.setName("test");
            profil.setTaxisurfUrl("http://taxigangsurf.appspot.com");
            ofy().save().entity(profil);

        }
        else
        {
            profil = profilList.get(0);
            logger.info("Using config profil:" + profil.getName());
        }
        return profil;
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

                    switch (booking.getOrderType())
                    {
                        case BOOKING:
                            boolean bookingForRoute = (booking.getRoute() == routeInfo.getId()) || (booking.getRoute() == routeInfo.getAssociatedRoute());
                            applies = bookingForRoute && (OrderStatus.PAID == booking.getStatus() && booking.getShareWanted());
                            break;
                        case SHARE:
                            break;
                        case SHARE_ANNOUNCEMENT:
                            applies = true;
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
        //            em.getTransaction().begin();
        //            em.persist(booking);
        //            em.getTransaction().commit();
        //
        //            em.detach(booking);
        //            bookingInfo = booking.getBookingInfo(getRouteInfo(booking.getRoute(), em));
        //
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
        //        return bookingInfo;
    }

    public BookingInfo getBooking(Long id)
    {
        return ofy().load().type(Booking.class).id(id).now().getInfo();

        //        EntityManager em = getEntityManager();
        //        Booking booking = em.find(Booking.class, id);
        //        em.detach(booking);
        //        return booking.getBookingInfo(getRouteInfo(booking.getRoute(), em));
    }

    public List<BookingInfo> getListFeedbackRequest()
    {
        throw new RuntimeException();

        //        EntityManager em = getEntityManager();
        //        List<BookingInfo> bookings = new ArrayList<>();
        //        try
        //        {
        //            @SuppressWarnings("unchecked")
        //            List<Booking> resultList = em.createQuery("select t from Booking t").getResultList();
        //            for (Booking booking : resultList)
        //            {
        //                if (booking.getRated() != null && !booking.getRated() && OrderStatus.PAID.equals(booking.getStatus()))
        //                {
        //                    DateTime bookingDate = new DateTime(booking.getDate());
        //                    if (bookingDate.plusDays(1).isBefore(DateTime.now()))
        //                    {
        //                        em.getTransaction().begin();
        //                        booking.setRated(true);
        //                        em.persist(booking);
        //                        em.getTransaction().commit();
        //                        em.detach(booking);
        //                        RouteInfo routeInfo = getRouteInfo(booking.getRoute(), em);
        //                        BookingInfo bookingInfo = booking.getBookingInfo(routeInfo);
        //                        bookings.add(bookingInfo);
        //                    }
        //                }
        //            }
        //        }
        //        catch (Exception ex)
        //        {
        //            logger.severe(ex.getMessage());
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
        //        return bookings;

    }

    public List<BookingInfo> getArchiveList()
    {
        List<Booking> resultList = ofy().load().type(Booking.class).list();
        List<BookingInfo> bookings = new ArrayList<>();
        for (Booking booking : resultList)
        {
            if (new DateTime(booking.getDate()).plusDays(7).isBefore(DateTime.now()))
            {
                bookings.add(booking.getInfo());
            }
        }
        return bookings;
    }

    public void archive(BookingInfo bookingInfo)
    {
        Booking booking = ofy().load().type(Booking.class).id(bookingInfo.getRouteInfo().getId()).now();
        ArchivedBooking achivedBooking = booking.getArchivedBooking();
        ofy().save().entity(achivedBooking).now();
        //        em.getTransaction().begin();
        //        em.persist(achivedBooking);
        //        em.getTransaction().commit();
        //        em.getTransaction().begin();
        //        em.remove(booking);
        //        em.getTransaction().commit();
    }

    public void cancel(BookingInfo bookingInfo)
    {
        Booking booking = ofy().load().type(Booking.class).id(bookingInfo.getId()).now();

        booking.setStatus(OrderStatus.CANCELED);
        ofy().save().entity(booking).now();
        //        em.getTransaction().begin();
        //        em.persist(booking);
        //        em.getTransaction().commit();
        //        em.detach(booking);
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
        return ofy().load().type(Agent.class).id(contractorInfo.getId()).now().getInfo();

        //        try
        //        {
        //            EntityManager em = getEntityManager();
        //            Agent agent = em.find(Agent.class, contractorInfo.getAgentId());
        //            if (agent != null)
        //            {
        //                return agent.getInfo();
        //            }
        //        }
        //        catch (Exception ex)
        //        {
        //            logger.severe(ex.getMessage());
        //        }
        //        return null;
    }

}
