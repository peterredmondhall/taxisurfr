package com.taxisurfr.client.steps;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.steps.ui.ShareStepUi;
import com.taxisurfr.shared.OrderType;

public class ShareStep implements WizardStep
{

    private final ShareStepUi ui;

    public ShareStep(Wizard wizard)
    {
        ui = new ShareStepUi(wizard);
    }

    @Override
    public String getCaption()
    {
        return Wizard.MOBILE ? "" : "Sharing";
    }

    @Override
    public Composite getContent()
    {
        return ui;
    }

    @Override
    public Boolean onNext()
    {
        ui.removeTable();
        return true;
    }

    @Override
    public Boolean onBack()
    {
        ui.removeTable();
        return true;
    }

    @Override
    public void clear()
    {

    }

    public void onNextShare()
    {
        Wizard.BOOKINGINFO.setOrderType(OrderType.SHARE);
        ui.removeTable();
    }

    @Override
    public void show(boolean visible, Button prev)
    {
        ui.show(visible, prev);
    }

}
