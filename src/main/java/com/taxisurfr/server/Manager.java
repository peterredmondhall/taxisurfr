package com.taxisurfr.server;

import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.ArugamEntity;
import com.taxisurfr.server.entity.ArugamImage;
import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.server.entity.Contractor;
import com.taxisurfr.server.entity.Rating;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.ArugamImageInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.Info;
import com.taxisurfr.shared.model.RatingInfo;
import com.taxisurfr.shared.model.RouteInfo;
import com.thoughtworks.xstream.XStream;

public class Manager<T extends Info, K extends ArugamEntity<?>>
{

    public void deleteAll(Class<?> entityType)
    {
        List<K> resultList = getAll(entityType);
        for (K entity : resultList)
        {
            ObjectifyService.ofy().delete().entity(entity).now();
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAllInfo(Class<?> entityType)
    {
        List<K> resultList = getAll(entityType);
        List<T> list = Lists.newArrayList();
        for (K entity : resultList)
        {
            list.add((T) entity.getInfo());
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<K> getAll(Class<?> entityType)
    {
        List<K> agents = (List<K>) ObjectifyService.ofy().load().type(entityType).list();
        return agents;
    }

    public void importDataset(String dataset, Class<?> type)
    {
        deleteAll(type);

        String[] datasets = dataset.split("<list>");
        for (String ds : datasets)
        {
            if (ds.contains(type.getSimpleName() + "Info"))
            {
                dataset = "<list>" + ds;
                break;
            }
        }

        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) new XStream().fromXML(dataset);
        for (T info : list)
        {
            if (type.equals(Route.class))
            {
                Route entity = Route.getRoute((RouteInfo) info);
                save(entity, type, info);
            }
            if (type.equals(ArugamImage.class))
            {
                ArugamImage entity = ArugamImage.getArugamImage((ArugamImageInfo) info);
                save(entity, type, info);
            }
            if (type.equals(Booking.class))
            {
                Booking entity = Booking.getBooking((BookingInfo) info, null);
                save(entity, type, info);
            }
            if (type.equals(Contractor.class))
            {
                Contractor entity = Contractor.getContractor((ContractorInfo) info);
                save(entity, type, info);
            }
            if (type.equals(Agent.class))
            {
                Agent entity = Agent.getAgent((AgentInfo) info);
                save(entity, type, info);
            }
            if (type.equals(Rating.class))
            {
                Rating entity = Rating.getRating((RatingInfo) info);
                save(entity, type, info);
            }
        }
    }

    public void save(ArugamEntity<?> entity, Class type, Info info)
    {
        ObjectifyService.ofy().save().entity(entity).now();

    }

    public String dump(Class<?> entityType)
    {
        return new XStream().toXML(getAllInfo(entityType));
    }

}
