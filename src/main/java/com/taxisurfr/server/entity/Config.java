package com.taxisurfr.server.entity;

import java.io.Serializable;



import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Config implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id Long id;
    private String profil;
    private Boolean maintenceAllowed;
    private String fbAppKey;
    private String fbAppSecret;
    private static Config config;

    public static Config getConfig()
    {
        return ObjectifyService.ofy().load().type(Config.class).first().now();
    }

    public String getProfil()
    {
        return profil;
    }

    public String getFbAppKey()
    {
        return fbAppKey;
    }

    public String getFbAppSecret()
    {
        return fbAppSecret;
    }

    public void setProfil(String profil)
    {
        this.profil = profil;
    }

    public Boolean getMaintenceAllowed()
    {
        return maintenceAllowed;
    }

    public void setMaintenceAllowed(Boolean maintenceAllowed)
    {
        this.maintenceAllowed = maintenceAllowed;
    }

}