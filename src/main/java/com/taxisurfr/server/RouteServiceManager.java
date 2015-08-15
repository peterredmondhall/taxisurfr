package com.taxisurfr.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.ArugamImage;
import com.taxisurfr.server.entity.Contractor;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.RouteInfo;

public class RouteServiceManager extends Manager
{
    private static final Logger logger = Logger.getLogger(RouteServiceManager.class.getName());

    public RouteServiceManager()
    {

        ObjectifyService.register(Route.class);
    }
    public List<RouteInfo> deleteRoute(AgentInfo userInfo, RouteInfo routeInfo) throws IllegalArgumentException
    {
        Route route = ObjectifyService.ofy().load().type(Route.class).id(routeInfo.getId()).now();
        route.setInactive();
        ObjectifyService.ofy().save().entity(route);
        final List<Route> all = getAll(Route.class);
        return getRoutes(userInfo);
    }

    public List<RouteInfo> saveRoute(AgentInfo userInfo, RouteInfo routeInfo, RouteInfo.SaveMode mode) throws IllegalArgumentException
    {
        addRoute(userInfo, routeInfo, mode);
        return getRoutes(userInfo);
    }

    public RouteInfo addRoute(AgentInfo userInfo, RouteInfo routeInfo, RouteInfo.SaveMode mode) throws IllegalArgumentException
    {
        Route route = null;
        switch (mode)
        {
            case ADD:
            case ADD_WITH_RETURN:
                route = new Route();
                persist(route, routeInfo);
                if (mode.equals(RouteInfo.SaveMode.ADD_WITH_RETURN))
                {
                    route = new Route();
                    String start = routeInfo.getEnd();
                    String end = routeInfo.getStart();
                    routeInfo.setStart(start);
                    routeInfo.setEnd(end);
                    routeInfo.setPickupType(RouteInfo.PickupType.HOTEL);
                    persist(route, routeInfo);
                }
                break;

            case UPDATE:
                route = ObjectifyService.ofy().load().type(Route.class).first().now();
                persist(route, routeInfo);
                break;
        }

    return route.getInfo();
}

    private void persist(Route route, RouteInfo routeInfo)
    {
        route.setStart(routeInfo.getStart());
        route.setEnd(routeInfo.getEnd());
        route.setCents(routeInfo.getCents());
        route.setAgentCents(routeInfo.getAgentCents());

        route.setPickupType(routeInfo.getPickupType());
        route.setImage(routeInfo.getImage());
        route.setDescription(routeInfo.getDescription());
        route.setContractorId(routeInfo.getContractorId());
        route.setAssociatedRoute(routeInfo.getAssociatedRoute());

        ObjectifyService.ofy().save().entity(route).now();
    }

    @SuppressWarnings("unchecked")
    public List<RouteInfo> getRoutes(AgentInfo agentInfo) throws IllegalArgumentException
    {
        List<RouteInfo> routes = new ArrayList<>();
        logger.info("getting routes for agent email " + agentInfo.getEmail() + " id " + agentInfo.getId());

        // find a list of providers being managed by this user
        List<Contractor> contractorList = ObjectifyService.ofy().load().type(Contractor.class).filter("agentId =", agentInfo.getId()).list();

        List<Long> contractorIdList = Lists.newArrayList();
        for (Contractor contractor : contractorList)
        {
            contractorIdList.add(contractor.getInfo().getId());
            logger.info("contractorId:" + contractor.getInfo().getId());
        }

        List<Route> resultList = ObjectifyService.ofy().load().type(Route.class).list();
        logger.info("get route count " + resultList.size());
        for (Route route : resultList)
        {
            RouteInfo routeInfo = route.getInfo();
            logger.info("routeInfo.getContractorId() " + routeInfo.getContractorId());

            if (agentInfo == null || contractorIdList.contains(routeInfo.getContractorId()))
            {
                if (!routeInfo.isInactive())
                {
                    routes.add(routeInfo);
                }
            }
        }
        logger.info("returning routeinfo count " + routes.size());

        return routes;

    }

    public List<RouteInfo> getRoutes() throws IllegalArgumentException
    {

        List<RouteInfo> routes = new ArrayList<>();
        List<Route> resultList = ObjectifyService.ofy().load().type(Route.class).list();

        logger.info("get all routes returned no. of routes" + resultList.size());
        for (Route route : resultList)
        {
            RouteInfo routeInfo = route.getInfo();
            if (!routeInfo.isInactive())
            {
                routes.add(routeInfo);
            }
        }
        return routes;

    }

    public RouteInfo getRoute(Long routeId)
    {
        RouteInfo routeInfo = null;
        Route route = ObjectifyService.ofy().load().type(Route.class).first().now();
        return route.getInfo();
    }

}
