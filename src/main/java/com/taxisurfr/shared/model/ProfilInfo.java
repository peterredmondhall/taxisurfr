package com.taxisurfr.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfilInfo implements IsSerializable
{
    private String paypalUrl;
    private String paypalAccount;
    private String stripePublishable;
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setStripePublishable(String stripePublishable)
    {
        this.stripePublishable = stripePublishable;
    }

    public String getStripePublishable()
    {
        return stripePublishable;
    }

    public String getPaypalUrl()
    {
        return paypalUrl;
    }

    public void setPaypalUrl(String paypalUrl)
    {
        this.paypalUrl = paypalUrl;
    }

    public String getPaypalAccount()
    {
        return paypalAccount;
    }

    public void setPaypalAccount(String paypalAccount)
    {
        this.paypalAccount = paypalAccount;
    }

    @Override
    public String toString()
    {
        return "[ProfilInfo paypalAccount=" + paypalAccount + "  paypalUrl=" + paypalUrl + "]";
    }
}
