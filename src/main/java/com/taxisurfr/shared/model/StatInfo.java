package com.taxisurfr.shared.model;

import com.taxisurfr.shared.Currency;

import java.util.Date;

public class StatInfo extends Info
{
    private String referer;
    private Date time;
    private Currency currency = Currency.USD;
    private Float currencyRate = 1F;

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public void setCurrencyRate(Float currencRate)
    {
        this.currencyRate = currencRate;
    }

    public Currency getCurrency()
    {
        return currency != null ? currency : Currency.USD;
    }

    public Float getCurrencyRate()
    {
        return currencyRate != null ? currencyRate : 1F;
    }

    public String getReferer()
    {
        return referer;
    }

    public void setReferer(String referrer)
    {
        this.referer = referer;
    }

    public enum Update
    {
        TYPE,
        ROUTE
    };

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String country;
    String detail;

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    String ip;
    StatInfo.Update update;

    public StatInfo.Update getUpdate()
    {
        return update;
    }

    public void setUpdate(StatInfo.Update update)
    {
        this.update = update;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    String src;

    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

}
