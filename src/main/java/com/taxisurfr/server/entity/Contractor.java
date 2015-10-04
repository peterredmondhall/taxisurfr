package com.taxisurfr.server.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.taxisurfr.shared.model.ContractorInfo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Entity
public class Contractor extends ArugamEntity<ContractorInfo>
{

    private static final long serialVersionUID = 1L;

    @Id public Long id;

    private String name;
    private String email;
    private Long mobile;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    private List<String> address;

    public List<String> getAddress()
    {
        return address;
    }

    public void setAddress(List<String> address)
    {
        this.address = address;
    }

    @Index
    private Long agentId;

    public static Contractor getContractor(ContractorInfo contractorInfo)
    {
        Contractor contractor = new Contractor();
        contractor.setName(contractorInfo.getName());
        contractor.setAgentId(contractorInfo.getAgentId());
        contractor.setAddress(contractorInfo.getAddress());
        contractor.setEmail(contractorInfo.getEmail());
        contractor.setMobile(contractorInfo.getMobile());
        return contractor;
    }

    public Long getAgentId()
    {
        return agentId;
    }

    public void setAgentId(Long userId)
    {
        this.agentId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public ContractorInfo getInfo()
    {
        ContractorInfo contractorInfo = new ContractorInfo();
        contractorInfo.setId(id);
        contractorInfo.setName(name);
        contractorInfo.setAgentId(agentId);
        contractorInfo.setEmail(email);
        contractorInfo.setMobile(mobile);
        List<String> addressList = newArrayList();
        contractorInfo.setAddress(addressList);
        if (address != null)
        {
            addressList.addAll(address);
        }
        return contractorInfo;
    }

    public void setMobile(Long mobile)
    {
        this.mobile = mobile;
    }
}