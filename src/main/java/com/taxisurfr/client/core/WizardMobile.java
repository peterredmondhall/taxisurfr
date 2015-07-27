package com.taxisurfr.client.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class WizardMobile extends Wizard
{
    private static WizardUiBinder uiBinder = GWT.create(WizardUiBinder.class);

    interface WizardUiBinder extends UiBinder<Widget, WizardMobile>
    {
    }

    @Override
    protected void initWidget()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
