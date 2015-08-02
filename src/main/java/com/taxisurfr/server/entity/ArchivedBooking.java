package com.taxisurfr.server.entity;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.OrderType;
import com.taxisurfr.shared.model.FinanceInfo;
import com.taxisurfr.shared.model.Info;

@Entity
public class ArchivedBooking<T extends Info, K extends ArugamEntity> extends ArugamEntity implements Serializable, Comparable<ArchivedBooking>
{

    private static final long serialVersionUID = 1L;

    @Id Long id;

    private Date date;
    private OrderStatus orderStatus;

    private String name;
    private String email;
    private String flightNo;
    private String landingTime;
    private int pax;
    private int surfboards;
    private String requirements;
    private String tx;
    private String ref;
    private Boolean shareWanted;
    private Date instanziated;
    private OrderType orderType;
    private Long route;

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Long getRoute()
    {
        return route;
    }

    public void setRoute(Long route)
    {
        this.route = route;
    }

    public Boolean getShareWanted()
    {
        return shareWanted;
    }

    public void setShareWanted(Boolean shareWanted)
    {
        this.shareWanted = shareWanted;
    }

    public OrderType getOrderType()
    {
        return orderType;
    }

    public void setOrderType(OrderType orderType)
    {
        this.orderType = orderType;
    }

    public Date getInstanziated()
    {
        return instanziated;
    }

    public void setInstanziated(Date instanziated)
    {
        this.instanziated = instanziated;
    }

    public void setStatus(OrderStatus status)
    {
        this.orderStatus = status;
    }

    public String getTx()
    {
        return tx;
    }

    public OrderStatus getStatus()
    {
        return orderStatus;
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

    public int getPax()
    {
        return pax;
    }

    public void setPax(int pax)
    {
        this.pax = pax;
    }

    public String getRequirements()
    {
        return requirements;
    }

    public void setRequirements(String requirements)
    {
        this.requirements = requirements;
    }

    public int getSurfboards()
    {
        return surfboards;
    }

    public void setSurfboards(int surfboards)
    {
        this.surfboards = surfboards;
    }

    public String getFlightNo()
    {
        return flightNo;
    }

    public void setFlightNo(String flightNo)
    {
        this.flightNo = flightNo;
    }

    public String getLandingTime()
    {
        return landingTime;
    }

    public void setLandingTime(String landingTime)
    {
        this.landingTime = landingTime;
    }

    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }

    private String parentRef;

    public String getParentRef()
    {
        return parentRef;
    }

    public void setParentRef(String parentRef)
    {
        this.parentRef = parentRef;
    }

    @Override
    public int compareTo(ArchivedBooking other)
    {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        return this.instanziated.after(instanziated) ? -1 : 1;
    }

    @Override
    public Info getInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public FinanceInfo getFinanceInfo()
    {
        FinanceInfo info = new FinanceInfo();
        info.setOrder(ref);
        info.setName(name);
        info.setDate(date);
        info.setBookingId(id);
        return info;
    }

}