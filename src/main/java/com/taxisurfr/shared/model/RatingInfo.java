package com.taxisurfr.shared.model;


public class RatingInfo extends Info
{
    private Long bookingId;
    private Long contractorId;

    public Long getContractorId()
    {
        return contractorId;
    }

    public void setContractorId(Long contractorId)
    {
        this.contractorId = contractorId;
    }

    public Long getBookingId()
    {
        return bookingId;
    }

    public void setBookingId(Long bookingId)
    {
        this.bookingId = bookingId;
    }

    private String critic;

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Integer getCleanliness()
    {
        return cleanliness;
    }

    public void setCleanliness(Integer cleanliness)
    {
        this.cleanliness = cleanliness;
    }

    public Integer getSafety()
    {
        return safety;
    }

    public void setSafety(Integer safety)
    {
        this.safety = safety;
    }

    public Integer getPunctuality()
    {
        return punctuality;
    }

    public void setPunctuality(Integer punctuality)
    {
        this.punctuality = punctuality;
    }

    public Integer getProfessionality()
    {
        return professionality;
    }

    public void setProfessionality(Integer professionality)
    {
        this.professionality = professionality;
    }

    private String author;

    Integer cleanliness;
    Integer safety;
    Integer punctuality;
    Integer professionality;

    public String getCritic()
    {
        return critic;
    }

    public void setCritic(String critic)
    {
        if (critic.length() > 400)
        {
            critic = critic.substring(0, 400);
        }
        this.critic = critic;
    }

    public int getAverage()
    {
        int total = 0;
        total += cleanliness != null ? cleanliness : 0;
        total += safety != null ? safety : 0;
        total += punctuality != null ? punctuality : 0;
        total += professionality != null ? professionality : 0;

        int average = (total / 4);
        int roundup = (total % 4) > 0 ? 1 : 0;
        return average + roundup;
    }
}
