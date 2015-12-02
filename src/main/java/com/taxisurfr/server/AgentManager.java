package com.taxisurfr.server;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.Contractor;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.AgentInfo;

import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class AgentManager extends Manager
{
    private static final Logger logger = Logger.getLogger(AgentManager.class.getName());

    public AgentManager()
    {

        ObjectifyService.register(Agent.class);
    }

    public Agent getAgent(Contractor contractor)
    {
        return  ofy().load().type(Agent.class).id(contractor.getAgentId()).now();
    }

    public AgentInfo createAgent(String agentEmail)
    {
        // check local user
        Objectify ofy = ObjectifyService.ofy();

        AgentInfo agentInfo = null;
            // To create new user for testing

        Agent agent = createAgent(agentEmail, false);

            return  agent.getInfo();

    }

    private Agent createAgent(String email,boolean admin)
    {

        Agent agent = ObjectifyService.ofy().load().type(Agent.class).filter("userEmail", email).first().now();
        if (agent == null)
        {
            agent = new Agent();
            agent.setEmail(email);
            agent.setAdmin(admin);
            ObjectifyService.ofy().save().entity(agent).now();
        }
        return agent;
    }

    public AgentInfo getAgent(String email)
    {
        AgentInfo agentInfo = null;
        Agent agent = ObjectifyService.ofy().load().type(Agent.class).filter("userEmail =", email).first().now();
        if (agent != null)
        {
            agentInfo = agent.getInfo();
            logger.info("getUser for email " + email + " returned " + agentInfo.getEmail() + "  " + agentInfo.getId() + " " + agentInfo.isAdmin());
        }
        return agentInfo;
    }

    public List<AgentInfo> getAgents()
    {
        List<AgentInfo> list = Lists.newArrayList();
        List<Agent> agents = ObjectifyService.ofy().load().type(Agent.class).list();

        for (Agent agent : agents)
        {
            list.add(agent.getInfo());
        }
        return list;

    }

}
