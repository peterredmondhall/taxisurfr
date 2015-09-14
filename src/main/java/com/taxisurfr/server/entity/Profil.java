package com.taxisurfr.server.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.taxisurfr.shared.model.ProfilInfo;

import java.io.Serializable;

@Entity
public class Profil implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id Long id;

    private boolean test;

    @Index
    private String name;
    private String paypalURL;
    private String paypalAccount;
    private String paypalAT;
    private String monitorEmail;
    private String contractorEmail;
    private String stripeSecret = "sk_test_TCIbuNPlBRe4VowPhqekTO1L";
    private String stripePublishable = "pk_test_rcKuNpP9OpTri7twmZ77UOI5";

    public String getStripeSecret()
    {
        return stripeSecret;
    }

    public void setStripeSecret(String stripeSecret)
    {
        this.stripeSecret = stripeSecret;
    }

    public String getStripePublishable()
    {
        return stripePublishable;
    }

    public void setStripePublishable(String stripePublishable)
    {
        this.stripePublishable = stripePublishable;
    }

    public String getContractorEmail()
    {
        return contractorEmail;
    }

    public void setContractorEmail(String contractorEmail)
    {
        this.contractorEmail = contractorEmail;
    }

    private String taxisurfUrl;

    private static final String TAXIGANGSURF_URL = "https://taxisurfr.com";

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isTest()
    {
        return test;
    }

    public void setTest(boolean test)
    {
        this.test = test;
    }

    public String getPaypalURL()
    {
        return paypalURL;
    }

    public void setPaypalURL(String paypalURL)
    {
        this.paypalURL = paypalURL;
    }

    public String getPaypalAccount()
    {
        return paypalAccount;
    }

    public void setPaypalAccount(String paypalAccount)
    {
        this.paypalAccount = paypalAccount;
    }

    public String getPaypalAT()
    {
        return paypalAT;
    }

    public void setPaypalAT(String paypalAT)
    {
        this.paypalAT = paypalAT;
    }

    public String getMonitorEmail()
    {
        return monitorEmail;
    }

    public void setMonitorEmail(String monitorEmail)
    {
        this.monitorEmail = monitorEmail;
    }

    public ProfilInfo getInfo()
    {
        ProfilInfo info = new ProfilInfo();
        info.setPaypalAccount(paypalAccount);
        info.setPaypalUrl(paypalURL);
        info.setStripePublishable(stripePublishable);
        info.setName(name);
        return info;
    }

    public String getTaxisurfUrl()
    {
        if (taxisurfUrl == null)
        {
            return TAXIGANGSURF_URL;

        }
        return taxisurfUrl;
    }

    public void setTaxisurfUrl(String taxisurfUrl)
    {
        this.taxisurfUrl = taxisurfUrl;
    }

}
