package com.taxisurfr.client.steps.ui.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class TimeBox extends Composite
{

    private static TimeBoxUiBinder uiBinder = GWT.create(TimeBoxUiBinder.class);

    interface TimeBoxUiBinder extends UiBinder<Widget, TimeBox>
    {
    }

    @UiField
    ListBox box;

    public TimeBox()
    {
        initWidget(uiBinder.createAndBindUi(this));
        for (int hour = 7; hour <= 22; hour++)
        {
            String time = hour + ":";
            box.addItem(time + "00");
            box.addItem(time + "15");
            box.addItem(time + "30");
            box.addItem(time + "45");
        }
    }

}
