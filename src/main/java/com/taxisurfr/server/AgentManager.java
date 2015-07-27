package com.taxisurfr.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.google.common.collect.Lists;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.shared.model.AgentInfo;

public class AgentManager extends Manager
{
    private static final Logger logger = Logger.getLogger(AgentManager.class.getName());

    public AgentInfo createAgent(String agentEmail)
    {
        // check local user
        EntityManager em = getEntityManager();
        AgentInfo agentInfo = null;
        try
        {
            // To create new user for testing
            createTestAgent(agentEmail, em);

            // TODO remove only for local testing
            createTestAgent("agent@example.com", em);

            Agent agent = (Agent) em.createQuery("select u from Agent u where u.userEmail = '" + agentEmail + "'").getSingleResult();
            agentInfo = agent.getInfo();
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        finally
        {
            em.close();
        }
        return agentInfo;
    }

    private void createTestAgent(String testAgent, EntityManager em)
    {

        try
        {
            em.createQuery("select u from Agent u where u.userEmail = '" + testAgent + "'").getSingleResult();
        }
        catch (NoResultException ex)
        {
            em.getTransaction().begin();
            Agent agent = new Agent();
            agent.setUserEmail(testAgent);
            agent.setAdmin(true);
            em.persist(agent);
            em.getTransaction().commit();

        }
    }

    public AgentInfo getAgent(String email)
    {

        EntityManager em = getEntityManager();
        AgentInfo agentInfo = null;
        try
        {
            Agent agent = (Agent) em.createQuery("select u from Agent u where u.userEmail = '" + email + "'").getSingleResult();
            agentInfo = agent.getInfo();
            logger.info("getUser for email " + email + " returned " + agentInfo.getEmail() + "  " + agentInfo.getId() + " " + agentInfo.isAdmin());
        }
        catch (NoResultException ex)
        {

        }
        finally
        {
            em.close();
        }
        return agentInfo;
    }

    public List<AgentInfo> getAgents()
    {
        List<AgentInfo> list = Lists.newArrayList();
        EntityManager em = getEntityManager();
        try
        {
            List<Agent> agents = em.createQuery("select u from Agent u ").getResultList();
            for (Agent agent : agents)
            {
                list.add(agent.getInfo());
            }
        }
        catch (NoResultException ex)
        {

        }
        finally
        {
            em.close();
        }
        return list;
    }

}
