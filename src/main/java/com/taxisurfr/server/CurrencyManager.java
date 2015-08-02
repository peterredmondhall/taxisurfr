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
        throw new RuntimeException();

        //        Float usRateBase = currencyUpdateList.get(Currency.USD);
        //        EntityManager em = getEntityManager();
        //        update(usRateBase, 1f, em, Currency.EUR);
        //        try
        //        {
        //            for (Currency currency : Currency.values())
        //            {
        //                if (currency != Currency.EUR)
        //                {
        //                    Float euroRate = currencyUpdateList.get(currency);
        //                    update(usRateBase, euroRate, em, currency);
        //                }
        //            }
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
    }

    //    private Float update(Float usRateBase, Float euroRate, EntityManager em, Currency currency)
    //    {
    //        CurrencyRate currencyRate;
    //        Float usRate = null;
    //        try
    //        {
    //            usRate = euroRate != null && usRateBase != null ? euroRate / usRateBase : null;
    //            currencyRate = (CurrencyRate) em.createQuery("select u from CurrencyRate u where u.code = '" + currency.name() + "'").getSingleResult();
    //            currencyRate.setRate(usRate);
    //            em.getTransaction().begin();
    //            em.persist(currencyRate);
    //            em.getTransaction().commit();
    //        }
    //        catch (NoResultException e)
    //        {
    //            currencyRate = new CurrencyRate();
    //            currencyRate.setCode(currency.name());
    //            currencyRate.setRate(usRate);
    //            em.getTransaction().begin();
    //            em.persist(currencyRate);
    //            em.getTransaction().commit();
    //        }
    //        return usRate;
    //    }

    public Float getRate(Currency currency)
    {
        Float rate = null;
        if (currency != null)
        {

            CurrencyRate currencyRate = ofy().load().type(CurrencyRate.class).filter("code =", currency.name()).first().now();
            if (currencyRate != null)
            {
                currencyRate.getRate();
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
