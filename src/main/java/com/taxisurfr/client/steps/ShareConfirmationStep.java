package com.taxisurfr.client.steps;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.steps.ui.ShareConfirmationStepUi;
import com.taxisurfr.shared.model.BookingInfo;

public class ShareConfirmationStep implements WizardStep
{

    private final ShareConfirmationStepUi ui;

    public ShareConfirmationStep()
    {
        ui = new ShareConfirmationStepUi();
    }

    @Override
    public String getCaption()
    {
        return "Sharing Confirmation";
    }

    @Override
    public Composite getContent()
    {
        return ui;
    }

    @Override
    public Boolean onNext()
    {
        return true;
    }

    @Override
    public Boolean onBack()
    {
        return true;
    }

    @Override
    public void clear()
    {

    }

    public void init(Button prev)
    {
        ui.show(true, prev);
    }

    public void setBookingInfo(List<BookingInfo> bookingInfo)
    {
        ui.setBookingInfo(bookingInfo);

    }

    @Override
    public void show(boolean visible, Button prev)
    {
        ui.show(visible, prev);
    }

}
