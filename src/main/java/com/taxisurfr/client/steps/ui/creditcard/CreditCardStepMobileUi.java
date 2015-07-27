package com.taxisurfr.client.steps.ui.creditcard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.steps.CreditCardStep;

public class CreditCardStepMobileUi extends CreditCardStepUi
{

    private static CreditCardSteUiUiBinder uiBinder = GWT.create(CreditCardSteUiUiBinder.class);

    interface CreditCardSteUiUiBinder extends UiBinder<Widget, CreditCardStepMobileUi>
    {
    }

    public CreditCardStepMobileUi(final CreditCardStep step)
    {
        super(step);
    }

    @Override
    protected void createUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
