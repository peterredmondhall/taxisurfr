package com.taxisurfr.client.steps.ui;

import com.google.gwt.user.client.ui.Button;

public class ButtonFactory
{

    public static Button getButton(String text)
    {
        Button b = new Button(text);
        b.setStyleName("btn btn-primary");
        return b;
    }

    public static Button getButton(String text, String width, String height)
    {
        Button b = getButton(text, width);
        b.setHeight(height);
        return b;
    }

    public static Button getButton(String text, String width)
    {
        Button b = getButton(text);
        b.setWidth(width);
        return b;
    }

}
