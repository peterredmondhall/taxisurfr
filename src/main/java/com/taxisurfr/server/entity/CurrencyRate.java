package com.taxisurfr.server.entity;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Id;
import com.taxisurfr.shared.model.AgentInfo;

@Entity
public class CurrencyRate extends ArugamEntity<AgentInfo>
{
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Float getRate()
    {
        return rate;
    }

    public void setRate(Float rate)
    {
        this.rate = rate;
    }

    private static final long serialVersionUID = 1L;
    @Id Long id;
    private String code;
    private Float rate;

    @Override
    public AgentInfo getInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
