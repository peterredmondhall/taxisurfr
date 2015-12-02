package com.taxisurfr.server.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.model.StatInfo;

import java.util.Date;

@Entity
public class SessionStat extends ArugamEntity<StatInfo>
{
    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }

    private static final long serialVersionUID = 1L;

    @Id Long id;

    String type;
    String route;
    Currency currency;
    Float currencyRate;
    String referer;
    String routeKey;
    Date time;

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    String cardToken;
    Long bookingId;

    @Index
    String ip;

    public String getRoute()
    {
        return route;
    }

    public void setRoute(String route)
    {
        this.route = route;
    }

    String src;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    String country;

    public static SessionStat getSessionStat(StatInfo statInfo)
    {
        SessionStat stat = new SessionStat();
        stat.referer = statInfo.getReferer();
        stat.routeKey = statInfo.getRouteKey();
        stat.time = statInfo.getTime();
        stat.setCountry(statInfo.getCountry());
        stat.setType(statInfo.getDetail());
        stat.src = statInfo.getSrc();
        stat.ip = statInfo.getIp();
        stat.currency = statInfo.getCurrency();
        stat.currencyRate = statInfo.getCurrencyRate();
        return stat;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    @Override
    public StatInfo getInfo()
    {
        StatInfo statInfo = new StatInfo();
        statInfo.setReferer(referer);
        statInfo.setRouteKey(routeKey);
        statInfo.setTime(time);
        statInfo.setCountry(country);
        statInfo.setSrc(src);
        statInfo.setIp(ip);
        statInfo.setCurrency(currency);
        statInfo.setCurrencyRate(currencyRate);
        return statInfo;
    }

}