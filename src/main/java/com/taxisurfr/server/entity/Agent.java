package com.taxisurfr.server.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.model.AgentInfo;

@Entity
public class Agent extends ArugamEntity<AgentInfo>
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String userEmail;
    private boolean admin;

    public Key getKey()
    {
        return key;
    }

    @Override
    public void setKey(Key key)
    {
        this.key = key;
    }

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
        agentInfo.setId(key.getId());
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
