package com.taxisurfr.shared.model;

import java.util.Date;

import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.OrderType;

public class BookingInfo extends Info
{
    private static final long serialVersionUID = 1L;

    private Date date;

    private String name;
    private String email;
    private String dateText;

    private String orderRef;
    private RouteInfo routeInfo;
    private Long routeId;
    private Long parentId;
    private Boolean shareWanted;
    private OrderStatus status;
    private OrderType orderType = OrderType.BOOKING;
    private String flightNo;
    private String landingTime;
    private int pax = 0;
    private int surfboards = 0;

    private String stripeRefusalReason;
    private Currency currency;
    private int paidPrice;

    private Float rate;

    public Long getRouteId()
    {
        return routeId;
    }

    public void setRouteId(Long routeId)
    {
        this.routeId = routeId;
    }

    public Boolean getShareWanted()
    {
        return shareWanted;
    }

    public void setShareWanted(Boolean shareWanted)
    {
        this.shareWanted = shareWanted;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public OrderType getOrderType()
    {
        return orderType;
    }

    public void setOrderType(OrderType orderType)
    {
        this.orderType = orderType;
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = status;
    }

    public String getLandingTime()
    {
        return landingTime;
    }

    public void setLandingTime(String landingTime)
    {
        this.landingTime = landingTime;
    }

    public String getFlightNo()
    {
        return flightNo;
    }

    public void setFlightNo(String flightNo)
    {
        this.flightNo = flightNo;
    }

    public void setSurfboards(int surfboards)
    {
        this.surfboards = surfboards;
    }

    private String requirements = "";

    public String getRequirements()
    {
        return requirements;
    }

    public void setRequirements(String requirements)
    {
        this.requirements = requirements;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public int getSurfboards()
    {
        return surfboards;
    }

    public int getPax()
    {
        return pax;
    }

    public void setPax(int pax)
    {
        this.pax = pax;
    }

    public RouteInfo getRouteInfo()
    {
        return routeInfo;
    }

    public void setRouteInfo(RouteInfo routeInfo)
    {
        this.routeInfo = routeInfo;
    }

    public String getStripeRefusalReason()
    {
        return stripeRefusalReason;
    }

    public void setStripeRefusalReason(String stripeRefusalReason)
    {
        this.stripeRefusalReason = stripeRefusalReason;
    }

    public String getOrderRef()
    {
        return orderRef;
    }

    public void setOrderRef(String orderRef)
    {
        this.orderRef = orderRef;
    }

    public String getDateText()
    {
        return dateText;
    }

    public void setDateText(String dateText)
    {
        this.dateText = dateText;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public Float getRate()
    {
        return rate;
    }

    public void setRate(Float rate)
    {
        this.rate = rate;
    }

    public int getPaidPrice()
    {
        return paidPrice;
    }

    public void setPaidPrice(int paidPrice)
    {
        this.paidPrice = paidPrice;
    }
}
