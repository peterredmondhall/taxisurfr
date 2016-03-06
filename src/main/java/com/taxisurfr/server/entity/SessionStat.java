package com.taxisurfr.server.entity;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
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
    String userAgent;
    Currency currency;
    Float currencyRate;
    Integer interactions = 0;
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Index
    String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Float getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(Float currencyRate) {
        this.currencyRate = currencyRate;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

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
        statInfo.setCurrency(currency);
        statInfo.setCurrencyRate(currencyRate);
        return statInfo;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void incInteractions(){
        interactions++;
    }
}