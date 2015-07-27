package com.taxisurfr.server.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.model.AgentInfo;

@Entity
public class ArugamApp extends ArugamEntity<AgentInfo>
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

    private final String fbAppKey = "1651399821757463";
    private final String fbAppSecret = "6183d291069a5ce8a7e449336ef521ad";

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
