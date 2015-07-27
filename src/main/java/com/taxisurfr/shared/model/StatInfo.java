package com.taxisurfr.shared.model;

public class StatInfo extends Info
{
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
    Long ident;

    public Long getIdent()
    {
        return ident;
    }

    public void setIdent(Long ident)
    {
        this.ident = ident;
    }

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
