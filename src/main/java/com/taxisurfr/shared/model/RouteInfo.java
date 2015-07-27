package com.taxisurfr.shared.model;

public class RouteInfo extends Info
{
    private static final long serialVersionUID = 1L;
    public static final Long PUBLIC = -1L;

    public enum PickupType
    {

        HOTEL("Hotel", "Pickup Time"),
        AIRPORT("Flight No", "Landing Time"),
        TRAINSTATION("Train station", "Train arrival time");

        String locationType;
        String timeType;

        private PickupType(String locationType, String timeType)
        {
            this.locationType = locationType;
            this.timeType = timeType;
        }

        public String getLocationType()
        {
            return locationType;
        }

        public String getTimeType()
        {
            return timeType;
        }
    }

    private PickupType pickupType;
    private Long cents;
    private Long agentCents;
    private Long associatedRoute;

    private boolean inactive;

    public Long getAgentCents()
    {
        return agentCents;
    }

    public void setAgentCents(Long agentCents)
    {
        this.agentCents = agentCents;
    }

    private Long image;

    public enum SaveMode
    {
        UPDATE,
        ADD,
        ADD_WITH_RETURN
    };

    private String start;
    private String end;
    private String description;
    private Long contractorId;

    public String getDescription()
    {
        return description;
    }

    public Long getContractorId()
    {
        return contractorId;
    }

    public void setContractorId(Long contactorId)
    {
        this.contractorId = contactorId;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getImage()
    {
        return image;
    }

    public void setImage(Long image)
    {
        this.image = image;
    }

    public String getStart()
    {
        return start;
    }

    public void setStart(String start)
    {
        this.start = start;
    }

    public String getEnd()
    {
        return end;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public PickupType getPickupType()
    {
        return pickupType;
    }

    public void setPickupType(PickupType pickupType)
    {
        this.pickupType = pickupType;
    }

    public Long getCents()
    {
        return cents;
    }

    public void setCents(Long cents)
    {
        this.cents = cents;
    }

    public String getKey(String price)
    {
        return getStart() + " to " + getEnd() + " " + price;
    }

    public Long getAssociatedRoute()
    {
        return associatedRoute;
    }

    public void setAssociatedRoute(Long associatedRoute)
    {
        this.associatedRoute = associatedRoute;
    }

    public boolean isInactive()
    {
        return inactive;
    }

    public void setInactive(boolean inactive)
    {
        this.inactive = inactive;
    }
}
