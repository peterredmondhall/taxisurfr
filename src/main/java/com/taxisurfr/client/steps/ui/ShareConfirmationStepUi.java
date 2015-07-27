package com.taxisurfr.client.steps.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.shared.model.BookingInfo;

public class ShareConfirmationStepUi extends Composite
{

    private static ShareConfirmationStepUiUiBinder uiBinder = GWT.create(ShareConfirmationStepUiUiBinder.class);

    interface ShareConfirmationStepUiUiBinder extends UiBinder<Widget, ShareConfirmationStepUi>
    {
    }

    @UiField
    HTMLPanel mainPanel;
    @UiField
    Label label1, label2;

    @UiField
    Label labelName, labelEmail, labelFlightNo, labelLandingTime, labelPax, labelSurfboards;

    public ShareConfirmationStepUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
        mainPanel.getElement().getStyle().setDisplay(Display.NONE);
    }

    public void show(boolean visible, Button prev)
    {
        mainPanel.setVisible(visible);
        mainPanel.getElement().getStyle().setDisplay(visible ? Display.BLOCK : Display.NONE);
        prev.setVisible(false);

    }

    @Override
    public void setHeight(String height)
    {
        super.setHeight(height);
    }

    @Override
    public void setWidth(String width)
    {
        super.setWidth(width);
    }

    public void setBookingInfo(List<BookingInfo> bookingInfo)
    {
        if (bookingInfo != null)
        {
            BookingInfo sharer = bookingInfo.get(1);
            label1.setText("Thank you agreeing to share your ride. Now, please get in contact directly with the person requesting the share.");
            label2.setText("Below are the details of your fellow passenger.");
            labelName.setText(sharer.getName());
            labelEmail.setText(sharer.getEmail());
            labelFlightNo.setText(sharer.getFlightNo());
            labelLandingTime.setText(sharer.getLandingTime());
            labelPax.setText("" + sharer.getPax());
            labelSurfboards.setText("" + sharer.getSurfboards());
        }
        else
        {
            label1.setText("An error has occured processing you order. Please contact dispatch@taxisurfr.com");
        }

    }
}
