package com.taxisurfr.shared.model;


public class AgentInfo extends Info
{
    private static final long serialVersionUID = 1L;

    private String email;
    private Long mobile;
    private boolean admin;

    public boolean isAdmin()
    {
        return admin;
    }

    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Long getMobile()
    {
        return mobile;
    }

    public void setMobile(Long mobile)
    {
        this.mobile = mobile;
    }
}
