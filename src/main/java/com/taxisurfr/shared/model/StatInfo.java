package com.taxisurfr.shared.model;

import com.taxisurfr.shared.Currency;

public class StatInfo extends Info
{
    private String sessionId;
    private Currency currency;
    private Float currencyRate;

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public void setCurrencyRate(Float currencRate)
    {
        this.currencyRate = currencRate;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public Float getCurrencyRate()
    {
        return currencyRate;
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
