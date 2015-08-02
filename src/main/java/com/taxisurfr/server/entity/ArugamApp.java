package com.taxisurfr.server.entity;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.model.AgentInfo;

@Entity
public class ArugamApp extends ArugamEntity<AgentInfo>
{
    private static final long serialVersionUID = 1L;
    @Id Long id;
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Key key;

    private final String fbAppKey = "1651399821757463";
    private final String fbAppSecret = "6183d291069a5ce8a7e449336ef521ad";

    @Override
    public AgentInfo getInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
