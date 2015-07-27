package com.taxisurfr.server;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.taxisurfr.server.entity.CurrencyRate;
import com.taxisurfr.shared.Currency;

public class CurrencyManager extends Manager
{

    public void update(Map<Currency, Float> currencyUpdateList)
    {
        Float usRateBase = currencyUpdateList.get(Currency.USD);
        EntityManager em = getEntityManager();
        update(usRateBase, 1f, em, Currency.EUR);
        try
        {
            for (Currency currency : Currency.values())
            {
                if (currency != Currency.EUR)
                {
                    Float euroRate = currencyUpdateList.get(currency);
                    update(usRateBase, euroRate, em, currency);
                }
            }
        }
        finally
        {
            em.close();
        }
    }

    private Float update(Float usRateBase, Float euroRate, EntityManager em, Currency currency)
    {
        CurrencyRate currencyRate;
        Float usRate = null;
        try
        {
            usRate = euroRate != null && usRateBase != null ? euroRate / usRateBase : null;
            currencyRate = (CurrencyRate) em.createQuery("select u from CurrencyRate u where u.code = '" + currency.name() + "'").getSingleResult();
            currencyRate.setRate(usRate);
            em.getTransaction().begin();
            em.persist(currencyRate);
            em.getTransaction().commit();
        }
        catch (NoResultException e)
        {
            currencyRate = new CurrencyRate();
            currencyRate.setCode(currency.name());
            currencyRate.setRate(usRate);
            em.getTransaction().begin();
            em.persist(currencyRate);
            em.getTransaction().commit();
        }
        return usRate;
    }

    public Float getRate(Currency currency)
    {
        if (currency == null)
        {
            return null;
        }
        Float rate = null;
        EntityManager em = getEntityManager();
        try
        {
            CurrencyRate currencyRate = (CurrencyRate) em.createQuery("select u from CurrencyRate u where u.code = '" + currency.name() + "'").getSingleResult();
            rate = currencyRate.getRate();
        }
        catch (NoResultException e)
        {
        }
        finally
        {
            em.close();
        }
        return rate;
    }
}
