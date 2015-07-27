package com.taxisurfr.server;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static org.joda.time.DateTime.now;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.OrderType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Predicate;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.ArchivedBooking;
import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.server.entity.Config;
import com.taxisurfr.server.entity.Contractor;
import com.taxisurfr.server.entity.Profil;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.ProfilInfo;
import com.taxisurfr.shared.model.RouteInfo;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class BookingServiceManager extends Manager
{
    private static final Logger logger = Logger.getLogger(BookingServiceManager.class.getName());
    static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");

    public BookingInfo addBookingWithClient(BookingInfo bookingInfo, String client) throws IllegalArgumentException
    {
        logger.info(bookingInfo.toString());
        EntityManager em = getEntityManager();
        try
        {
            Route route = em.find(Route.class, bookingInfo.getRouteInfo().getId());

            Booking booking = Booking.getBooking(bookingInfo, client);
            em.getTransaction().begin();
            em.persist(booking);
            em.getTransaction().commit();
            em.detach(booking);

            bookingInfo = booking.getBookingInfo(route.getInfo());

            booking = em.find(Booking.class, bookingInfo.getId());
            booking.setRef(booking.generateRef());
            em.getTransaction().begin();
            em.persist(booking);
            em.getTransaction().commit();
            em.detach(booking);

            bookingInfo = booking.getBookingInfo(route.getInfo());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            em.close();
        }
        try
        {
            if (bookingInfo.getOrderType().equals(OrderType.SHARE_ANNOUNCEMENT))
            {
                Mailer.sendShareAnnouncement(bookingInfo, getProfil());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return bookingInfo;
    }

    private RouteInfo getRouteInfo(long id, EntityManager em)
    {
        Route route = em.find(Route.class, id);
        if (route == null)
        {
            return null;
        }
        return route.getInfo();
    }

    private static final long ALL_BOOKINGS = -1;

    public List<BookingInfo> getBookings()
    {
        return getBookings(ALL_BOOKINGS);
    }

    public List<BookingInfo> getBookings(Long agentId) throws IllegalArgumentException
    {
        EntityManager em = getEntityManager();
        List<BookingInfo> bookings = new ArrayList<>();
        try
        {
            @SuppressWarnings("unchecked")
            List<Booking> resultList = em.createQuery("select t from Booking t order by instanziated desc").getResultList();
            for (Booking booking : resultList)
            {
                em.detach(booking);
                RouteInfo routeInfo = getRouteInfo(booking.getRoute(), em);
                if (routeInfo != null)
                {
                    BookingInfo bookingInfo = booking.getBookingInfo(routeInfo);
                    if (bookingInfo != null)
                    {
                        Contractor contractor = em.find(Contractor.class, routeInfo.getContractorId());
                        if (contractor != null)
                        {
                            if (agentId == ALL_BOOKINGS || contractor.getAgentId().equals(agentId))
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
        }
        catch (Exception ex)
        {
            logger.severe(ex.getMessage());
        }
        finally
        {
            em.close();
        }
        return bookings;
    }

    public BookingInfo getBookingForTransactionWithClient(Profil profil, String client, OrderStatus hasPaid) throws IllegalArgumentException
    {

        EntityManager em = getEntityManager();
        BookingInfo bookingInfo = null;
        try
        {
            String query = "select t from Booking t where client='" + client + "' order by instanziated desc";
            @SuppressWarnings("unchecked")
            List<Booking> resultList = em.createQuery(query).getResultList();
            if (resultList.size() > 0)
            {
                Booking booking = resultList.get(0);
                booking.setStatus(hasPaid);
                em.getTransaction().begin();
                em.persist(booking);
                em.getTransaction().commit();

                em.detach(booking);
                bookingInfo = resultList.get(0).getBookingInfo(getRouteInfo(resultList.get(0).getRoute(), em));
            }
        }
        finally
        {
            em.close();
        }
        return bookingInfo;
    }

    public BookingInfo setBookingRef(BookingInfo bookingInfo)
    {
        Booking booking = getEntityManager().find(Booking.class, bookingInfo.getId());
        bookingInfo.setOrderRef(booking.getRef());
        return bookingInfo;
    }

    public BookingInfo setPayed(Profil profil, BookingInfo bi, OrderStatus orderStatus) throws IllegalArgumentException
    {

        EntityManager em = getEntityManager();
        BookingInfo bookingInfo = null;
        try
        {
            Booking booking = em.find(Booking.class, bi.getId());

            booking.setStatus(orderStatus);
            em.getTransaction().begin();
            em.persist(booking);
            em.getTransaction().commit();

            em.detach(booking);
            bookingInfo = booking.getBookingInfo(getRouteInfo(booking.getRoute(), em));
        }
        finally
        {
            em.close();
        }
        return bookingInfo;
    }

    public ProfilInfo getPaypalProfil() throws IllegalArgumentException
    {
        ProfilInfo profilInfo = getProfil().getInfo();
        logger.info(profilInfo.toString());
        return profilInfo;

    }

    public boolean getMaintenceAllowed()
    {
        EntityManager em = getEntityManager();
        boolean maintenanceAllowed = false;
        try
        {
            Config config = Config.getConfig(em);
            if (Config.getConfig(em).getMaintenceAllowed() == null)
            {
                logger.info("maintence allowed not avail - setting false");
                em.getTransaction().begin();
                config.setMaintenceAllowed(false);
                em.persist(config);
                em.getTransaction().commit();
                em.detach(config);
            }
            else
            {
                maintenanceAllowed = config.getMaintenceAllowed();
            }
        }
        finally
        {
            em.close();
        }
        return maintenanceAllowed;
    }

    public Profil getProfil()
    {
        EntityManager em = getEntityManager();
        Profil profil = null;
        try
        {
            Config config = null;
            List<Config> configList = em.createQuery("select t from Config t").getResultList();
            if (configList.size() == 0)
            {
                em.getTransaction().begin();
                ;
                config = new Config();
                config.setProfil("test");
                config.setMaintenceAllowed(true);
                em.persist(config);
                em.getTransaction().commit();
            }
            else
            {
                config = configList.get(0);
            }
            logger.info("Using config profil:" + config.getProfil());

            List<Profil> profilList = em.createQuery("select t from Profil t where name ='" + config.getProfil() + "'").getResultList();

            if (profilList.size() == 0)
            {
                em.getTransaction().begin();
                profil = new Profil();
                profil.setPaypalAccount(PaypalPaymentChecker.TEST_ACCT);
                profil.setPaypalAT(PaypalPaymentChecker.TEST_AT);
                profil.setPaypalURL(PaypalPaymentChecker.TEST_PAYPAL_URL);
                profil.setTest(true);
                profil.setName("test");
                profil.setTaxisurfUrl("http://taxigangsurf.appspot.com");
                em.persist(profil);
                em.getTransaction().commit();

            }
            else
            {
                profil = profilList.get(0);
                logger.info("Using config profil:" + profil.getName());
            }
        }
        finally
        {
            em.close();
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
    public List<BookingInfo> getBookingsForRoute(RouteInfo routeInfo) throws IllegalArgumentException
    {
        Predicate<Booking> accept = new Predicate<Booking>()
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
                            applies = OrderStatus.PAID == booking.getStatus() && booking.getShareWanted();
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

        EntityManager em = getEntityManager();
        List<BookingInfo> bookings = new ArrayList<>();
        try
        {
            int counter = 0;
            String where = "where ";
            where += "route=" + routeInfo.getId();
            if (routeInfo.getAssociatedRoute() != null && routeInfo.getAssociatedRoute() != Route.NO_ASSOCIATED)
            {
                where += " or route=" + routeInfo.getAssociatedRoute();
            }
            @SuppressWarnings("unchecked")
            List<Booking> result = em.createQuery("select t from Booking t " + where).getResultList();
            List<Booking> current = newArrayList(from(result).filter(accept));
            for (Booking booking : current)
            {
                em.detach(booking);
                bookings.add(booking.getBookingInfo(getRouteInfo(booking.getRoute(), em)));
            }
            Collections.sort(bookings, new BookingInfoComparator());
            logger.info("share candidates size = " + current.size());
        }
        finally
        {
            em.close();
        }

        return bookings;
    }

    public BookingInfo setShareAccepted(BookingInfo bookingInfo)
    {
        EntityManager em = getEntityManager();
        try
        {
            Booking booking = em.find(Booking.class, bookingInfo.getId());

            booking.setStatus(OrderStatus.SHARE_ACCEPTED);
            em.getTransaction().begin();
            em.persist(booking);
            em.getTransaction().commit();

            em.detach(booking);
            bookingInfo = booking.getBookingInfo(getRouteInfo(booking.getRoute(), em));

        }
        finally
        {
            em.close();
        }
        return bookingInfo;
    }

    public BookingInfo getBooking(Long id)
    {
        EntityManager em = getEntityManager();
        Booking booking = em.find(Booking.class, id);
        em.detach(booking);
        return booking.getBookingInfo(getRouteInfo(booking.getRoute(), em));
    }

    public List<BookingInfo> getListFeedbackRequest()
    {
        EntityManager em = getEntityManager();
        List<BookingInfo> bookings = new ArrayList<>();
        try
        {
            @SuppressWarnings("unchecked")
            List<Booking> resultList = em.createQuery("select t from Booking t").getResultList();
            for (Booking booking : resultList)
            {
                if (booking.getRated() != null && !booking.getRated() && OrderStatus.PAID.equals(booking.getStatus()))
                {
                    DateTime bookingDate = new DateTime(booking.getDate());
                    if (bookingDate.plusDays(1).isBefore(DateTime.now()))
                    {
                        em.getTransaction().begin();
                        booking.setRated(true);
                        em.persist(booking);
                        em.getTransaction().commit();
                        em.detach(booking);
                        RouteInfo routeInfo = getRouteInfo(booking.getRoute(), em);
                        BookingInfo bookingInfo = booking.getBookingInfo(routeInfo);
                        bookings.add(bookingInfo);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.severe(ex.getMessage());
        }
        finally
        {
            em.close();
        }
        return bookings;

    }

    public List<BookingInfo> getArchiveList()
    {
        EntityManager em = getEntityManager();
        List<BookingInfo> bookings = new ArrayList<>();
        try
        {
            @SuppressWarnings("unchecked")
            List<Booking> resultList = em.createQuery("select t from Booking t").getResultList();
            for (Booking booking : resultList)
            {
                if (new DateTime(booking.getDate()).plusDays(7).isBefore(DateTime.now()))
                {
                    em.detach(booking);
                    bookings.add(booking.getInfo());
                }
            }
        }
        catch (Exception ex)
        {
            logger.severe(ex.getMessage());
        }
        finally
        {
            em.close();
        }
        return bookings;

    }

    public void archive(BookingInfo bookingInfo)
    {
        EntityManager em = getEntityManager();
        Booking booking = em.find(Booking.class, bookingInfo.getId());
        ArchivedBooking achivedBooking = booking.getArchivedBooking();
        em.getTransaction().begin();
        em.persist(achivedBooking);
        em.getTransaction().commit();
        em.getTransaction().begin();
        em.remove(booking);
        em.getTransaction().commit();
    }

    public void cancel(BookingInfo bookingInfo)
    {
        EntityManager em = getEntityManager();
        Booking booking = em.find(Booking.class, bookingInfo.getId());
        booking.setStatus(OrderStatus.CANCELED);
        em.getTransaction().begin();
        em.persist(booking);
        em.getTransaction().commit();
        em.detach(booking);
    }

    public ContractorInfo getContractor(BookingInfo bookingInfo)
    {
        try
        {
            EntityManager em = getEntityManager();
            Route route = em.find(Route.class, bookingInfo.getRouteInfo().getId());
            if (route != null)
            {
                Contractor contractor = em.find(Contractor.class, route.getContractorId());
                return contractor.getInfo();
            }
        }
        catch (Exception ex)
        {
            logger.severe(ex.getMessage());
        }
        return null;
    }

    public AgentInfo getAgent(ContractorInfo contractorInfo)
    {
        try
        {
            EntityManager em = getEntityManager();
            Agent agent = em.find(Agent.class, contractorInfo.getAgentId());
            if (agent != null)
            {
                return agent.getInfo();
            }
        }
        catch (Exception ex)
        {
            logger.severe(ex.getMessage());
        }
        return null;
    }

}
