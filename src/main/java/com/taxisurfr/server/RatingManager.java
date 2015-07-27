package com.taxisurfr.server;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.server.entity.Rating;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.RatingInfo;
import com.taxisurfr.shared.model.RouteInfo;

public class RatingManager extends Manager
{
    private static final Logger logger = Logger.getLogger(RatingManager.class.getName());

    public void add(RatingInfo ratingInfo)
    {
        EntityManager em = getEntityManager();
        try
        {
            Booking booking = em.find(Booking.class, ratingInfo.getBookingId());
            if (booking != null)
            {
                Route route = em.find(Route.class, booking.getRoute());
                if (route != null)
                {
                    ratingInfo.setContractorId(route.getContractorId());
                    em.getTransaction().begin();
                    Rating rating = Rating.getRating(ratingInfo);
                    em.persist(rating);
                    em.getTransaction().commit();
                }
                else
                {
                    logger.severe("persisiting ratingInfo: could find route:" + booking.getRoute());
                }
            }
            else
            {
                logger.severe("persisiting ratingInfo: could find booking:" + ratingInfo.getBookingId());
            }

        }
        catch (Exception ex)
        {
            logger.severe("persisiting ratingInfo:" + ex);
        }

    }

    public List<RatingInfo> getRatings(RouteInfo routeInfo)
    {

        EntityManager em = getEntityManager();
        List<RatingInfo> ratings = newArrayList();

        try
        {
            List<Rating> resultList = em.createQuery("select t from Rating t where contractorId=" + routeInfo.getContractorId()).getResultList();
            for (Rating rating : resultList)
            {
                ratings.add(rating.getInfo());
            }
        }
        catch (Exception ex)
        {
            logger.severe("getting ratings");
        }
        return ratings;
    }
}
