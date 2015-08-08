package com.taxisurfr.server;

import java.util.Map;

import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.*;
import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.model.RouteInfo;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class CurrencyManager extends Manager
{
    public CurrencyManager()
    {
        ObjectifyService.register(CurrencyRate.class);
    }

    public void update(Map<Currency, Float> currencyUpdateList)
    {

        Float usRateBase = currencyUpdateList.get(Currency.USD);
        update(usRateBase, 1f, Currency.EUR);
        for (Currency currency : Currency.values())
        {
            if (currency != Currency.EUR)
            {
                Float euroRate = currencyUpdateList.get(currency);
                update(usRateBase, euroRate, currency);
            }
        }
    }

    private Float update(Float usRateBase, Float euroRate, Currency currency)
    {
        CurrencyRate currencyRate;
        Float usRate = null;
        usRate = euroRate != null && usRateBase != null ? euroRate / usRateBase : null;
        currencyRate = ObjectifyService.ofy().load().type(CurrencyRate.class).filter("code =", currency.name()).first().now();
        if (currencyRate == null)
        {
            currencyRate = new CurrencyRate();
            currencyRate.setCode(currency.name());
        }
        currencyRate.setRate(usRate);
        ofy().save().entity(currencyRate).now();
        return usRate;
    }

    public Float getRate(Currency currency)
    {
        Float rate = null;
        if (currency != null)
        {

            CurrencyRate currencyRate = ofy().load().type(CurrencyRate.class).filter("code =", currency.name()).first().now();
            if (currencyRate != null)
            {
                rate =currencyRate.getRate();
            }
        }
        return rate;

        //        Float rate = null;
        //        EntityManager em = getEntityManager();
        //        try
        //        {
        //            CurrencyRate currencyRate = (CurrencyRate) em.createQuery("select u from CurrencyRate u where u.code = '" + currency.name() + "'").getSingleResult();
        //            rate = currencyRate.getRate();
        //        }
        //        catch (NoResultException e)
        //        {
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
        //        return rate;
    }
}
