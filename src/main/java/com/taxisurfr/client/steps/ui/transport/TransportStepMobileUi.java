package com.taxisurfr.client.steps.ui.transport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.core.Wizard;

public class TransportStepMobileUi extends TransportStepUi
{
    public TransportStepMobileUi(Wizard wizard)
    {
        super(wizard);
    }

    private static TransportStepMobileUiBinder uiBinder = GWT.create(TransportStepMobileUiBinder.class);

    interface TransportStepMobileUiBinder extends UiBinder<Widget, TransportStepMobileUi>
    {
    }

    @Override
    protected void createUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
