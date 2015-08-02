package com.taxisurfr.server.entity;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Index;
import com.taxisurfr.shared.model.AgentInfo;

@Entity
public class Agent extends ArugamEntity<AgentInfo>
{
    private static final long serialVersionUID = 1L;
    @Id public Long id;

    @Index
    private String userEmail;
    private boolean admin;

    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    @Override
    public AgentInfo getInfo()
    {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setId(id);
        agentInfo.setEmail(userEmail);
        agentInfo.setAdmin(admin);
        return agentInfo;

    }

    public static Agent getAgent(AgentInfo agentInfo)
    {
        Agent agent = new Agent();
        agent.setUserEmail(agentInfo.getEmail());
        return agent;

    }

    public void setAdmin(boolean b)
    {
        this.admin = b;
    }
}
