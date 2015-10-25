package com.taxisurfr.client.steps;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.TaxisurfrEntryPoint;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.steps.ui.transport.TransportStepMobileUi;
import com.taxisurfr.client.steps.ui.transport.TransportStepUi;

public class TransportStep implements WizardStep
{

    private final TransportStepUi ui;

    public TransportStep(Wizard wizard)
    {
        if (Wizard.MOBILE)
        {
            ui = new TransportStepMobileUi(wizard);
        }
        else
        {
            ui = new TransportStepUi(wizard);
        }
    }

    @Override
    public String getCaption()
    {
        return Wizard.MOBILE ? "" : TaxisurfrEntryPoint.MESSAGES.firstPage();
    }

    @Override
    public Composite getContent()
    {
        return ui;
    }

    public void init(Button prev)
    {
        ui.show(true, prev);
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

    @Override
    public void show(boolean visible, Button prev)
    {
        ui.show(visible, prev);
    }

    public void displayRoute(String key)
    {
        ui.displayRoute(key);
    }

    public void setLoaded()
    {
        ui.setLoaded(true);
    }
}
