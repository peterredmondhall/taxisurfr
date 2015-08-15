package com.taxisurfr.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.*;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.RouteInfo;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ContractorManager extends Manager
{
    private static final Logger logger = Logger.getLogger(ContractorManager.class.getName());

    public ContractorManager()
    {
        ObjectifyService.register(Contractor.class);

    }

    public ContractorInfo createContractor(ContractorInfo contractorInfo)
    {

        Contractor contractor = Contractor.getContractor(contractorInfo);
        ObjectifyService.ofy().save().entity(contractor).now();
        return contractor.getInfo();
    }

//    public ContractorInfo getContractor(Long contractorId) throws IllegalArgumentException
//    {
//        ObjectifyService.ofy().load().
//
//        //        EntityManager em = getEntityManager();
//        //        ContractorInfo contractorInfo = null;
//        //        {
//        //            try
//        //            {
//        //                contractorInfo = em.find(Contractor.class, contractorId).getInfo();
//        //            }
//        //            catch (Exception e)
//        //            {
//        //                logger.log(Level.SEVERE, e.getMessage(), e);
//        //            }
//        //            finally
//        //            {
//        //                em.close();
//        //            }
//        //        }
//        //        return contractorInfo;
//
//    }

    @SuppressWarnings("unchecked")
    public List<ContractorInfo> getContractors(AgentInfo agentInfo)
    {
        List<ContractorInfo> list = Lists.newArrayList();
        List<Contractor> contractors;
        if (agentInfo == null)
        {
            contractors = ObjectifyService.ofy().load().type(Contractor.class).list();
        }
        else
        {
            contractors = ObjectifyService.ofy().load().type(Contractor.class).filter("agentId =", agentInfo.getId()).list();
        }

        for (Contractor contractor : contractors)
        {
            list.add(contractor.getInfo());
        }
        return list;
        //        logger.info("getting contractors for agent email " + agentInfo.getEmail() + " id " + agentInfo.getId());
        //
        //        // check local user
        //        EntityManager em = getEntityManager();
        //        List<ContractorInfo> list = Lists.newArrayList();
        //        try
        //        {
        //            @SuppressWarnings("unchecked")
        //            List<Contractor> contractorList;
        //            if (agentInfo != null)
        //            {
        //                contractorList = em.createQuery("select t from Contractor t where agentId=" + agentInfo.getId()).getResultList();
        //            }
        //            else
        //            {
        //                contractorList = em.createQuery("select t from Contractor t ").getResultList();
        //            }
        //            logger.info("query returned " + contractorList.size());
        //            for (Contractor contractor : contractorList)
        //            {
        //                list.add(contractor.getInfo());
        //            }
        //        }
        //        catch (Exception e)
        //        {
        //            logger.log(Level.SEVERE, e.getMessage(), e);
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
        //        return list;

    }

    public void delete(ContractorInfo contractorInfo)
    {
        throw new RuntimeException();

        //        EntityManager em = getEntityManager();
        //        try
        //        {
        //            em.getTransaction().begin();
        //            Contractor contractor = em.find(Contractor.class, contractorInfo.getId());
        //            em.remove(contractor);
        //            em.getTransaction().commit();
        //        }
        //        catch (Exception e)
        //        {
        //            logger.log(Level.SEVERE, e.getMessage(), e);
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
    }

    public List<ContractorInfo> deleteContractor(AgentInfo agentInfo, ContractorInfo contractorInfo)
    {
        Contractor contractor = ofy().load().type(Contractor.class).id(contractorInfo.getId()).now();

        ofy().delete().entity(contractor).now();
        return getContractors(agentInfo);

        //        List<ContractorInfo> contractors = null;
        //        EntityManager em = getEntityManager();
        //        try
        //        {
        //            Contractor contractor = em.find(Contractor.class, contractorInfo.getId());
        //            em.getTransaction().begin();
        //            em.remove(contractor);
        //            em.getTransaction().commit();
        //            contractor = em.find(Contractor.class, contractorInfo.getId());
        //        }
        //        catch (Exception e)
        //        {
        //            logger.severe("deleting route");
        //        }
        //        finally
        //        {
        //            em.close();
        //        }
        //        return contractors;
    }

    public List<ContractorInfo> saveContractor(AgentInfo agentInfo, ContractorInfo contractorInfo, ContractorInfo.SaveMode mode) throws IllegalArgumentException
    {
        List<ContractorInfo> routes = null;
        contractorInfo.setAgentId(agentInfo.getId());
        Contractor contractor;
        switch (mode)
        {
            case ADD:
                contractor = new Contractor();
                persist(contractor, contractorInfo);
                break;

            case UPDATE:
                contractor = ofy().load().type(Contractor.class).id(contractorInfo.getId()).now();
                persist(contractor, contractorInfo);
                break;
        }

        return getContractors(agentInfo);

    }

    private void persist(Contractor contractor, ContractorInfo contractorInfo)
    {
        contractor.setName(contractorInfo.getName());
        contractor.setEmail(contractorInfo.getEmail());
        contractor.setAgentId(contractorInfo.getAgentId());
        contractor.setAddress(contractorInfo.getAddress());

        ofy().save().entity(contractor).now();
    }
}
