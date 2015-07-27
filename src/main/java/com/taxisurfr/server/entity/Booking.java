package com.taxisurfr.server.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.OrderType;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.FinanceInfo;
import com.taxisurfr.shared.model.Info;
import com.taxisurfr.shared.model.RouteInfo;

@Entity
public class Booking<T extends Info, K extends ArugamEntity> extends ArugamEntity implements Serializable, Comparable<Booking>
{

    public Booking()
    {
        orderStatus = OrderStatus.BOOKED;
        orderType = OrderType.BOOKING;
        instanziated = new Date();
        rated = false;
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

    @Override
    public void setKey(Key key)
    {
        this.key = key;
    }

    private Date date;

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    private String name;
    private String email;
    private String dateText;
    private String flightNo;
    private String landingTime;
    private int pax;
    private int surfboards;
    private OrderStatus orderStatus;
    private String requirements;
    private String tx;
    private String ref;
    private String client;
    private Boolean shareWanted;
    private Date instanziated;
    private OrderType orderType;
    private Long parentId;
    private Long route;
    private Boolean rated;
    private Currency currency;
    private int paidPrice;

    public int getPaidPrice()
    {
        return paidPrice;
    }

    public void setPaidPrice(int paidPrice)
    {
        this.paidPrice = paidPrice;
    }

    public Long getRoute()
    {
        return route;
    }

    public void setRoute(Long route)
    {
        this.route = route;
    }

    public Boolean getRated()
    {
        return rated;
    }

    public void setRated(boolean rated)
    {
        this.rated = rated;
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

    public String getClient()
    {
        return client;
    }

    public void setClient(String client)
    {
        this.client = client;
    }

    public void setTx(String tx)
    {
        this.tx = tx;
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

    public static Booking getBooking(BookingInfo bookingInfo, String client)
    {
        Booking booking = new Booking();
        booking.setDate(bookingInfo.getDate());
        booking.setDateText(bookingInfo.getDateText());
        booking.setEmail(bookingInfo.getEmail());
        booking.setName(bookingInfo.getName());
        booking.setFlightNo(bookingInfo.getFlightNo());
        booking.setLandingTime(bookingInfo.getLandingTime());
        booking.setPax(bookingInfo.getPax());
        booking.setSurfboards(bookingInfo.getSurfboards());
        booking.setRequirements(bookingInfo.getRequirements());
        booking.setClient(client);
        booking.setShareWanted(bookingInfo.getShareWanted());
        booking.setParentId(bookingInfo.getParentId());
        booking.setOrderType(bookingInfo.getOrderType() != null ? bookingInfo.getOrderType() : OrderType.BOOKING);
        booking.setRoute(bookingInfo.getRouteId());
        booking.setCurrency(bookingInfo.getCurrency());
        booking.setPaidPrice(bookingInfo.getPaidPrice());

        return booking;
    }

    public ArchivedBooking getArchivedBooking()
    {
        ArchivedBooking booking = new ArchivedBooking();
        booking.setDate(date);
        booking.setEmail(email);
        booking.setName(name);
        booking.setFlightNo(flightNo);
        booking.setLandingTime(landingTime);
        booking.setPax(pax);
        booking.setSurfboards(surfboards);
        booking.setRequirements(requirements);
        booking.setShareWanted(shareWanted);
        booking.setOrderType(orderType);
        booking.setStatus(orderStatus);
        booking.setRoute(route);
        booking.setRef(ref);

        return booking;
    }

    @Override
    public BookingInfo getInfo()
    {
        return getBookingInfo(null);
    }

    public BookingInfo getBookingInfo(RouteInfo routeInfo)
    {
        BookingInfo bookingInfo = new BookingInfo();
        bookingInfo.setId(key.getId());
        bookingInfo.setDate(getDate());
        bookingInfo.setDateText(getDateText());
        bookingInfo.setEmail(getEmail());
        bookingInfo.setName(getName());
        bookingInfo.setFlightNo(getFlightNo());
        bookingInfo.setLandingTime(getLandingTime());
        bookingInfo.setPax(getPax());
        bookingInfo.setSurfboards(getSurfboards());
        bookingInfo.setRequirements(getRequirements());
        bookingInfo.setParentId(getParentId());
        bookingInfo.setStatus(getStatus());
        bookingInfo.setOrderType(getOrderType());
        bookingInfo.setShareWanted(getShareWanted());
        bookingInfo.setRouteInfo(routeInfo);
        bookingInfo.setRouteId(route);
        bookingInfo.setOrderRef(ref);
        bookingInfo.setCurrency(currency);
        bookingInfo.setPaidPrice(paidPrice);
        return bookingInfo;
    }

    public String getDateText()
    {
        return dateText;
    }

    public void setDateText(String dateText)
    {
        this.dateText = dateText;
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

    // deprecated
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
    public int compareTo(Booking other)
    {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        return this.instanziated.after(instanziated) ? -1 : -1;
    }

    public FinanceInfo getFinanceInfo()
    {
        FinanceInfo info = new FinanceInfo();
        info.setName(name);
        info.setDate(date);
        info.setId(key.getId());
        return info;
    }

    public String generateRef()
    {
        String idString = Long.toString(key.getId());
        int len = idString.length();
        if (len > 5)
        {
            return idString.substring(len - 5, len - 1);
        }
        else
            return idString;
    }

    private void setCurrency(Currency currency)
    {
        this.currency = currency;
    }
}