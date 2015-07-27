package com.taxisurfr.server;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.Route;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.taxisurfr.server.entity.Contractor;
import com.taxisurfr.server.entity.Finance;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.FinanceInfo;

public class FinanceManager extends Manager
{
    private static final Logger logger = Logger.getLogger(FinanceManager.class.getName());

    public class FinanceInfoComparator implements Comparator<FinanceInfo>
    {

        @Override
        public int compare(FinanceInfo bi1, FinanceInfo bi2)
        {
            return (new DateTime(bi1.getDate()).isAfter(new DateTime(bi2.getDate()))) ? -1 : 1;
        }
    }

    public List<FinanceInfo> getFinance(AgentInfo agentInfo)
    {
        List<FinanceInfo> list = Lists.newArrayList();
        EntityManager em = getEntityManager();
        try
        {
            String query = "select t from Finance t";
            @SuppressWarnings("unchecked")
            List<Finance> resultList = em.createQuery(query).getResultList();
            if (resultList.size() == 0)
            {
                Finance transfer = new Finance();
                em.getTransaction().begin();
                em.persist(transfer);
                em.getTransaction().commit();
                em.detach(transfer);
            }
            else
            {
                query = "select t from Finance t where agentId=" + agentInfo.getId();
                resultList = em.createQuery(query).getResultList();
                for (Finance transfer : resultList)
                {
                    em.detach(transfer);
                    FinanceInfo info = transfer.getInfo();
                    list.add(transfer.getInfo());
                }
            }

        }
        finally
        {
            em.close();
        }
        Collections.sort(list, new FinanceInfoComparator());
        return list;
    }

    public List<FinanceInfo> addTransfer(FinanceInfo financeInfo)
    {
        EntityManager em = getEntityManager();

        try
        {
            Finance finance = new Finance();
            finance.setAgentId(financeInfo.getAgentId());
            finance.setType(FinanceInfo.Type.TRANSFER);
            finance.setDate(financeInfo.getDate());
            finance.setName(financeInfo.getName());
            finance.setAmount(financeInfo.getAmount());
            finance.setBookingId(null);
            finance.setOrderRef(null);

            em.getTransaction().begin();
            em.persist(finance);
            em.getTransaction().commit();
            em.detach(finance);
        }
        finally
        {
            em.close();
        }
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setId(financeInfo.getAgentId());
        return getFinance(agentInfo);
    }

    public void addPayment(BookingInfo bookingInfo, Date date)
    {
        Route route = getEntityManager().find(Route.class, bookingInfo.getRouteId());
        Contractor contractor = getEntityManager().find(Contractor.class, route.getContractorId());
        Agent agent = getEntityManager().find(Agent.class, contractor.getAgentId());
        AgentInfo agentInfo = agent.getInfo();
        Long amount = route.getAgentCents() != null ? route.getAgentCents() : (long) (route.getCents() * 0.90);
        logger.info("addpayment:" + amount);

        EntityManager em = getEntityManager();

        try
        {
            Finance finance = new Finance();
            finance.setAgentId(agentInfo.getId());
            finance.setType(FinanceInfo.Type.PAYMENT);
            finance.setDate(date);
            finance.setName(bookingInfo.getName());
            finance.setAmount(amount);
            finance.setBookingId(bookingInfo.getId());
            finance.setOrderRef(bookingInfo.getOrderRef());
            finance.setAgentId(agentInfo.getId());
            finance.setDeliveryDate(bookingInfo.getDate());

            em.getTransaction().begin();
            em.persist(finance);
            em.getTransaction().commit();
            em.detach(finance);
        }
        finally
        {
            em.close();
        }

    }

    public void cancel(Long bookingId)
    {
        EntityManager em = getEntityManager();
        try
        {
            String query = "select t from Finance t where bookingId=" + bookingId;
            @SuppressWarnings("unchecked")
            Finance finance = (Finance) em.createQuery(query).getSingleResult();
            em.getTransaction().begin();
            finance.setAmount(0L);
            em.persist(finance);
            em.getTransaction().commit();

        }
        catch (Exception thrown)
        {
            logger.log(Level.SEVERE, "finance not found for bookingId" + bookingId, thrown);
        }
        finally
        {
            em.close();
        }
    }

}
