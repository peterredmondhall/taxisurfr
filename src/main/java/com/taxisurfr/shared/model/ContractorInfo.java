package com.taxisurfr.shared.model;

import java.util.List;

public class ContractorInfo extends Info
{
    public enum SaveMode
    {
        UPDATE,
        ADD
    };

    private String name;
    private String email;

    private List<String> address;

    public List<String> getAddress()
    {
        return address;
    }

    public void setAddress(List<String> address)
    {
        this.address = address;
    }

    private Long agentId;

    public Long getAgentId()
    {
        return agentId;
    }

    public void setAgentId(Long agentId)
    {
        this.agentId = agentId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
