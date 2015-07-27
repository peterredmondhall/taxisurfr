package com.taxisurfr.server.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Config implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String profil;
    private Boolean maintenceAllowed;
    private  String fbAppKey;
    private  String fbAppSecret;

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

    public static Config getConfig(EntityManager em)
    {
        return (Config) em.createQuery("select t from Config t").getSingleResult();
    }
}
