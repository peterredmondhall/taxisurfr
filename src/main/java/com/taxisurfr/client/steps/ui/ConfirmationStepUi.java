package com.taxisurfr.client.steps.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.model.BookingInfo;

public class ConfirmationStepUi extends Composite
{

    private static ConfirmationStepUiUiBinder uiBinder = GWT.create(ConfirmationStepUiUiBinder.class);

    interface ConfirmationStepUiUiBinder extends UiBinder<Widget, ConfirmationStepUi>
    {
    }

    @UiField
    HTMLPanel mainPanel;
    @UiField
    Label labelConfirmationEmail, label1, label2;

    @UiField
    Panel panelOrderForm, panelShareLink;

    @UiField
    Anchor orderForm, shareLink;

    public ConfirmationStepUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
        mainPanel.getElement().getStyle().setDisplay(Display.NONE);
    }

    public void show(boolean visible, Button prev)
    {
        mainPanel.setVisible(visible);
        mainPanel.getElement().getStyle().setDisplay(visible ? Display.BLOCK : Display.NONE);
        prev.setVisible(false);
        setBookingInfo(Wizard.getBookingInfo());
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

    public void setRatingFeedbackConfirmation()
    {
        label1.setText("Thank you for your feedback.");
        label2.setText("We look forward seeing you next season.");
        labelConfirmationEmail.setText("");

    }

    public void setBookingInfo(BookingInfo bookingInfo)
    {
        panelOrderForm.setVisible(false);
        panelShareLink.setVisible(false);
        labelConfirmationEmail.setVisible(false);
        String orderFormUrl = "/orderform?order=" + bookingInfo.getId();
        String shareUrl = "?route=" + bookingInfo.getRouteId();

        orderForm.setHref(orderFormUrl);
        shareLink.setHref(shareUrl);
        panelShareLink.setVisible(bookingInfo.getShareWanted());

        if (bookingInfo != null)
        {
            switch (bookingInfo.getOrderType())
            {
                case BOOKING:
                    if (OrderStatus.PAID.equals(bookingInfo.getStatus()))
                    {
                        label1.setText("Thank you for your order.");
                        label2.setText("Please find your order form in the link below.");
                        labelConfirmationEmail.setText("A confirmation email has been sent to " + bookingInfo.getEmail());
                        labelConfirmationEmail.setVisible(true);
                        panelOrderForm.setVisible(true);
                    }
                    else
                        setError();
                    break;
                case SHARE:
                    if (OrderStatus.SHARE_ACCEPTED.equals(bookingInfo.getStatus()))
                    {
                        label1.setText("Thank you for accepting the share request");
                        label2.setText("A message has been sent to the person requesting the share and they will contact you directly.");
                        panelOrderForm.setVisible(false);
                    }
                    else
                    {
                        label1.setText("Thank you for your share request");
                        label2.setText("We will contact you with their contact details of the other person once they have accepted the share");
                        panelOrderForm.setVisible(false);

                    }
                    break;
                case SHARE_ANNOUNCEMENT:
                    label1.setText("You have created a share announcement.");
                    panelOrderForm.setVisible(false);
                    panelShareLink.setVisible(true);
                    break;
                default:
                    break;

            }

        }
        else
        {
        }

    }

    private void setError()
    {
        label1.setText("An error has occured processing you order. Please contact dispatch@taxisurfr.com");
        label2.setText("");

    }
}
