package com.taxisurfr.server;

import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.Contractor;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.RouteInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RouteServiceManager extends Manager
{
    private static final Logger logger = Logger.getLogger(RouteServiceManager.class.getName());

    private static List<Route> routeInfoCache;

    public RouteServiceManager()
    {

        ObjectifyService.register(Route.class);
    }
    public List<RouteInfo> deleteRoute(AgentInfo userInfo, RouteInfo routeInfo) throws IllegalArgumentException
    {
        Route route = ObjectifyService.ofy().load().type(Route.class).id(routeInfo.getId()).now();
        route.setInactive(true);
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

    public List<RouteInfo> getRoutes(String query) throws IllegalArgumentException
    {
        logger.info("query routes" + query);
        query = query.toUpperCase();
        List<RouteInfo> routes = new ArrayList<>();
        if (routeInfoCache == null)
        {
            routeInfoCache = ObjectifyService.ofy().load().type(Route.class).filter("inactive !=", true).list();
            logger.info("get all routes returned no. of routes" + routeInfoCache.size());
        }
        else
        {
            logger.info("routes from cache no. of routes" + routeInfoCache.size());
        }
        List<Route> resultList = routeInfoCache;

        for (Route route : resultList)
        {
            RouteInfo routeInfo = route.getInfo();
            if (!routeInfo.isInactive() && routeInfo.getStart().toUpperCase().startsWith(query))
            {
                routes.add(routeInfo);
            }
        }
        logger.info("get queried routes returned no. of routes" + routes.size());
        return routes;

    }

    public RouteInfo getRoute(Long routeId)
    {
        return ObjectifyService.ofy().load().type(Route.class).id(routeId).now().getInfo();
    }

    public static void resetCache()
    {
        routeInfoCache = null;
    }

    public void loadall()
    {
        Long contractorid = null;
        ContractorManager manager = new ContractorManager();
        for (Object obj : manager.getAll(Contractor.class))
        {
            Contractor contractor = (Contractor) obj;
            if ("dispatch@taxisurfr.com".equals(contractor.getEmail()))
            {
                contractorid = contractor.id;
                break;
            }
        }
        logger.info("contractor id" + contractorid);
        if (contractorid == null)
        {
            throw new RuntimeException("");
        }

        List<Route> list = ObjectifyService.ofy().load().type(Route.class).list();
        for (Route route : list)
        {
            if (route.getInactive())
            {
                route.setInactive(false);
                route.setContractorId(contractorid);
                ObjectifyService.ofy().save().entity(route).now();
                logger.info("route activated" + route.getStart() + " " + route.getEnd());
            }
            else
            {
                route.setInactive(false);
                ObjectifyService.ofy().save().entity(route).now();
            }
        }
        logger.info("active routes" + list.size());

    }
}
