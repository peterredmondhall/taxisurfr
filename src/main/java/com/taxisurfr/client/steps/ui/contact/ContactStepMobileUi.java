package com.taxisurfr.client.steps.ui.contact;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.core.Wizard;

public class ContactStepMobileUi extends ContactStepUi
{
    public ContactStepMobileUi(Wizard wizard)
    {
        super(wizard);
    }

    private static ContactStepUiUiBinder uiBinder = GWT.create(ContactStepUiUiBinder.class);

    interface ContactStepUiUiBinder extends UiBinder<Widget, ContactStepMobileUi>
    {
    }

    @Override
    protected void createUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setErrorMsg(String msg, ErrorMsg errorMsg)
    {
        dateMsg.removeStyleName("errMsg");
        firstNameMsg.removeStyleName("errMsg");
        lastNameMsg.removeStyleName("errMsg");
        labelEmailMsg.removeStyleName("errMsg");
        labelEmail2Msg.removeStyleName("errMsg");
        labelFlightNo.removeStyleName("errMsg");
        labelFlightLandingTime.removeStyleName("errMsg");
        switch (errorMsg)
        {
            case DATE:
                dateMsg.addStyleName("errMsg");
                break;
            case FIRST_NAME:
                firstNameMsg.addStyleName("errMsg");
                break;
            case LAST_NAME:
                lastNameMsg.addStyleName("errMsg");
                break;
            case EMAIL:
                labelEmailMsg.addStyleName("errMsg");
                break;
            case EMAIL2:
                labelEmail2Msg.addStyleName("errMsg");
                break;
            case FLIGHTNO:
                labelFlightNo.addStyleName("errMsg");
                break;
            case ARRIVAL:
                labelFlightLandingTime.addStyleName("errMsg");
                break;
        }
    }

}
