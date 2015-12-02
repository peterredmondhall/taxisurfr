package com.taxisurfr.server.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.taxisurfr.shared.model.AgentInfo;

@Entity
public class Agent extends ArugamEntity<AgentInfo>
{
    private static final long serialVersionUID = 1L;
    @Id public Long id;

    @Index
    private String email;

    private Long mobile;
    private boolean admin;
    private Long orderCount;
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public AgentInfo getInfo()
    {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setId(id);
        agentInfo.setEmail(email);
        agentInfo.setMobile(mobile);
        agentInfo.setAdmin(admin);
        return agentInfo;

    }

    public Long getOrderCount()
    {
        if (orderCount==null)
        {
            orderCount = 0L;
        }
        return orderCount++;
    }

    public static Agent getAgent(AgentInfo agentInfo)
    {
        Agent agent = new Agent();
        agent.setEmail(agentInfo.getEmail());
        agent.setMobile(agentInfo.getMobile());
        return agent;

    }

    public void setAdmin(boolean b)
    {
        this.admin = b;
    }

    public void setMobile(Long mobile)
    {
        this.mobile = mobile;
    }
    public Long getMobile() {
        return mobile;
    }

}
