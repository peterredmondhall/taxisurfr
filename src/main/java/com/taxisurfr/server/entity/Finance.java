package com.taxisurfr.server.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.model.FinanceInfo;

@Entity
public class Finance extends ArugamEntity<FinanceInfo>
{
    private static final long serialVersionUID = 1L;
    @Id Long id;
    private Date date;
    private Date deliveryDate;

    public static final Double AGENT_MARGIN = 0.90;
    private Long bookingId;

    private Long amount;
    private Long agentId;
    private String name;
    private String orderRef;
    private FinanceInfo.Type type;

    public FinanceInfo.Type getType()
    {
        return type;
    }

    public void setType(FinanceInfo.Type type)
    {
        this.type = type;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Long getAmount()
    {
        return amount;
    }

    public void setAmount(Long amount)
    {
        this.amount = amount;
    }

    public Long getAgentId()
    {
        return agentId;
    }

    public void setAgentId(Long agentId)
    {
        this.agentId = agentId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public FinanceInfo getInfo()
    {
        FinanceInfo info = new FinanceInfo();
        info.setType(type);
        info.setName(name);
        info.setDate(date);
        info.setAmount(amount);
        info.setOrder(orderRef);
        info.setBookingId(bookingId);
        info.setAgentId(agentId);
        info.setDeliveryDate(deliveryDate);
        return info;
    }

    public void setOrderRef(String orderRef)
    {
        this.orderRef = orderRef;

    }

    public Long getBookingId()
    {
        return bookingId;
    }

    public void setBookingId(Long bookingId)
    {
        this.bookingId = bookingId;
    }

    public void setDeliveryDate(Date date)
    {
        this.deliveryDate = date;
    }
}
