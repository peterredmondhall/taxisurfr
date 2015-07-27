package com.taxisurfr.client.steps.ui;

import com.google.gwt.user.client.ui.ListBox;

public class StepUtil
{
    public static void initTimeBox(ListBox timeBox)
    {
        for (int hour = 7; hour <= 22; hour++)
        {
            String time = hour + ":";
            timeBox.addItem(time + "00");
            timeBox.addItem(time + "15");
            timeBox.addItem(time + "30");
            timeBox.addItem(time + "45");
        }
    }

    public static void initPaxBox(ListBox box)
    {
        for (int pax = 0; pax <= 20; pax++)
        {
            box.addItem("" + pax);
        }
    }

}
