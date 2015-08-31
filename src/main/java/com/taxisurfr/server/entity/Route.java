package com.taxisurfr.server.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.taxisurfr.shared.model.RouteInfo;

@Entity
public class Route extends ArugamEntity<RouteInfo>
{
    private static final long serialVersionUID = 1L;
    public static final long NO_ASSOCIATED = 0L;


    @Id Long id;

    @Index
    private String start;
    private String end;
    private String description;
    private Long contractorId;
    private RouteInfo.PickupType pickupType;
    private Long cents;
    private Long agentCents;
    private Long image;
    private boolean inactive;
    private Long associatedRoute = NO_ASSOCIATED;

    public Long getContractorId()
    {
        return contractorId;
    }

    public void setContractorId(Long contractorId)
    {
        this.contractorId = contractorId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getStart()
    {
        return start;
    }

    public void setStart(String start)
    {
        this.start = start;
    }

    public Long getImage()
    {
        return image;
    }

    public void setImage(Long image)
    {
        this.image = image;
    }

    public String getEnd()
    {
        return end;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public RouteInfo.PickupType getPickupType()
    {
        return pickupType;
    }

    public void setPickupType(RouteInfo.PickupType pickupType)
    {
        this.pickupType = pickupType;
    }

    public void setCents(Long cents)
    {
        this.cents = cents;
    }

    public Long getCents()
    {
        return cents;
    }


    public static Route getRoute(RouteInfo routeInfo)
    {
        Route route = new Route();
        route.setStart(routeInfo.getStart());
        route.setEnd(routeInfo.getEnd());
        route.setDescription(routeInfo.getDescription());
        route.setCents(routeInfo.getCents());
        route.setAgentCents(routeInfo.getAgentCents());
        route.setPickupType(routeInfo.getPickupType());
        route.setImage(routeInfo.getImage());
        route.setContractorId(routeInfo.getContractorId());
        route.setAssociatedRoute(routeInfo.getAssociatedRoute());
        return route;
    }

    @Override
    public RouteInfo getInfo()
    {
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId(id);
        routeInfo.setStart(start);
        routeInfo.setEnd(end);
        routeInfo.setDescription(description);
        routeInfo.setCents(cents);
        routeInfo.setAgentCents(agentCents);
        routeInfo.setInactive(inactive);
        routeInfo.setAssociatedRoute(associatedRoute);

        routeInfo.setPickupType(pickupType);
        routeInfo.setImage(image);
        if (contractorId == null)
        {
            routeInfo.setContractorId(4840028442198016L);
        }
        else
        {
            routeInfo.setContractorId(contractorId);
        }
        return routeInfo;
    }

    public void setAssociatedRoute(Long associatedRoute)
    {
        this.associatedRoute = associatedRoute;
    }

    public Long getAgentCents()
    {
        return agentCents;
    }

    public void setAgentCents(Long agentCents)
    {
        this.agentCents = agentCents;
    }

    public void setInactive()
    {
        inactive = true;
    }

    public Long getAssociatedRoute()
    {
        return associatedRoute;
    }

}