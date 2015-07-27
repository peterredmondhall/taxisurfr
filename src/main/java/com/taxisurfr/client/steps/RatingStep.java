package com.taxisurfr.client.steps;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.steps.ui.RatingStepUi;

public class RatingStep implements WizardStep
{

    private final RatingStepUi ui;

    public RatingStep()
    {
        ui = new RatingStepUi();
    }

    @Override
    public String getCaption()
    {
        return "Feedback";
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

}
