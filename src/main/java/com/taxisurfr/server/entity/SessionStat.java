package com.taxisurfr.server.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.taxisurfr.shared.model.StatInfo;

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
    @Index
    String sessionId;

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
        stat.setCountry(statInfo.getCountry());
        stat.setType(statInfo.getDetail());
        stat.src = statInfo.getSrc();
        stat.ip = statInfo.getIp();
        stat.sessionId = statInfo.getSessionId();

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
        // TODO
        return null;
    }

}