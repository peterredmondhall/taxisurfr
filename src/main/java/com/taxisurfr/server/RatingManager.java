package com.taxisurfr.server;

import static com.google.common.collect.Lists.newArrayList;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.server.entity.Rating;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.RatingInfo;
import com.taxisurfr.shared.model.RouteInfo;

public class RatingManager extends Manager
{
    private static final Logger logger = Logger.getLogger(RatingManager.class.getName());

    public void add(RatingInfo ratingInfo)
    {
        Booking booking = ofy().load().type(Booking.class).id(ratingInfo.getBookingId()).now();

            if (booking != null)
            {
                Route route = ofy().load().type(Route.class).id(booking.getRoute()).now();
                if (route != null)
                {
                    ratingInfo.setContractorId(route.getContractorId());
                    Rating rating = Rating.getRating(ratingInfo);
                    ofy().save().entity(rating);
                }
            }
    }

    final Function<Rating, RatingInfo> RATING_TO_INFO = new Function<Rating, RatingInfo>()
    {
        @Override
        public RatingInfo apply(Rating rating)
        {
            return rating.getInfo();
        }
    };

    public List<RatingInfo> getRatings(RouteInfo routeInfo)
    {
        List<Rating> ratings = ofy().load().type(Rating.class).filter("contractorId =", routeInfo.getContractorId()).list();
        return FluentIterable.from(ratings).transform(RATING_TO_INFO).toList();

//        EntityManager em = getEntityManager();
//        List<RatingInfo> ratings = newArrayList();
//
//        try
//        {
//            List<Rating> resultList = em.createQuery("select t from Rating t where contractorId=" + routeInfo.getContractorId()).getResultList();
//            for (Rating rating : resultList)
//            {
//                ratings.add(rating.getInfo());
//            }
//        }
//        catch (Exception ex)
//        {
//            logger.severe("getting ratings");
//        }
//        return ratings;
    }
}
