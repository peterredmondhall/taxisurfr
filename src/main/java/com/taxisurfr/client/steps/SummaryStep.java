package com.taxisurfr.client.steps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.client.steps.ui.summary.SummaryStepMobileUi;
import com.taxisurfr.client.steps.ui.summary.SummaryStepUi;
import com.taxisurfr.shared.OrderType;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.client.GwtWizard;

public class SummaryStep implements WizardStep
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);

    private final SummaryStepUi ui;

    public SummaryStep(Wizard wizard)
    {
        if (Wizard.MOBILE)
        {
            ui = new SummaryStepMobileUi(wizard);
        }
        else
        {
            ui = new SummaryStepUi(wizard);
        }
    }

    @Override
    public String getCaption()
    {
        return Wizard.MOBILE ? "" : GwtWizard.MESSAGES.fourthPage();
    }

    @Override
    public Composite getContent()
    {
        return ui;
    }

    @Override
    public Boolean onNext()
    {
        if (Wizard.BOOKINGINFO.getOrderType() == OrderType.SHARE)
        {
            service.sendShareRequest(Wizard.BOOKINGINFO, new AsyncCallback<BookingInfo>()
            {

                @Override
                public void onFailure(Throwable caught)
                {
                    Refresh.refresh();
                }

                @Override
                public void onSuccess(BookingInfo bi)
                {
                    Wizard.BOOKINGINFO = bi;
                }
            });
        }

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

    @Override
    public void show(boolean visible, Button prev)
    {
        ui.show(visible, prev);
    }

}
