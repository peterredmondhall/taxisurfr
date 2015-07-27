package com.taxisurfr.client.steps;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.steps.ui.ConfirmationStepUi;
import com.taxisurfr.client.GwtWizard;

public class PaymentStep implements WizardStep
{

    private final ConfirmationStepUi ui;

    public PaymentStep()
    {
        ui = new ConfirmationStepUi();
    }

    @Override
    public String getCaption()
    {
        return Wizard.MOBILE ? "" : GwtWizard.MESSAGES.fifthPage();
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

    @Override
    public void show(boolean visible, Button prev)
    {
        ui.show(visible, prev);
    }

}
