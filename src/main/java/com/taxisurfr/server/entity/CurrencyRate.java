package com.taxisurfr.server.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String code;
    private Float rate;

    public Key getKey()
    {
        return key;
    }

    @Override
    public void setKey(Key key)
    {
        this.key = key;
    }

    @Override
    public AgentInfo getInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
