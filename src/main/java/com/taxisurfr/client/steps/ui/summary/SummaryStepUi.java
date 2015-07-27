package com.taxisurfr.client.steps.ui.summary;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.shared.CurrencyHelper;
import com.taxisurfr.shared.OrderType;

import static com.taxisurfr.shared.OrderType.BOOKING;

public class SummaryStepUi extends Composite
{
    private static SummaryStepUiUiBinder uiBinder = GWT.create(SummaryStepUiUiBinder.class);
    private static DateTimeFormat sdf = DateTimeFormat.getFormat("dd.MM.yyyy");

    interface SummaryStepUiUiBinder extends UiBinder<Widget, SummaryStepUi>
    {
    }

    @UiField
    HTMLPanel mainPanel;

    @UiField
    Label summaryTitle, summaryDetails, labelEmail, labelName, labelSurfboards, labelPax, labelFlightNo, labelLandingTime, labelDate, labelPrice, labelRequirements, labelInterestedSharing, labelRPT;

    @UiField
    Label labelPickupDetail, labelPickupTimeDetail;

    @UiField
    Label labelInterestedSharingField, labelRequirementsField;
//    @UiField
//    Paypal paypal;

    @UiField
    Label pay1;

    @UiField
    Button nextButtonSummary;

    public SummaryStepUi(final Wizard wizard)
    {
        createUi();
        mainPanel.getElement().getStyle().setDisplay(Display.NONE);
        // paypal.setUrl();
        // stripe1.setVisible(true);

        nextButtonSummary.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                wizard.onNextClick(null);
            }
        });

    }

    protected void createUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void show(boolean visible, Button prev)
    {
        mainPanel.setVisible(visible);
        mainPanel.getElement().getStyle().setDisplay(visible ? Display.BLOCK : Display.NONE);

        labelDate.setText(sdf.format(Wizard.BOOKINGINFO.getDate()));
        labelFlightNo.setText(Wizard.BOOKINGINFO.getFlightNo());
        labelLandingTime.setText(Wizard.BOOKINGINFO.getLandingTime());

        labelPickupDetail.setText(Wizard.ROUTEINFO.getPickupType().getLocationType());
        labelPickupTimeDetail.setText(Wizard.ROUTEINFO.getPickupType().getTimeType());

        labelPax.setText(Integer.toString(Wizard.BOOKINGINFO.getPax()));
        labelSurfboards.setText(Integer.toString(Wizard.BOOKINGINFO.getSurfboards()));
        labelEmail.setText(Wizard.BOOKINGINFO.getEmail());
        labelName.setText(Wizard.BOOKINGINFO.getName());
        labelRequirements.setText(Wizard.BOOKINGINFO.getRequirements());
        labelPrice.setText(CurrencyHelper.getPrice(Wizard.BOOKINGINFO.getRouteInfo(), Wizard.BOOKINGINFO.getCurrency(), Wizard.BOOKINGINFO.getRate()));
        labelInterestedSharing.setText(Wizard.BOOKINGINFO.getShareWanted() ? "yes please" : "no, thanks");
        prev.setEnabled(true);

        labelInterestedSharing.setVisible(true);
        labelInterestedSharingField.setVisible(true);
        labelRequirementsField.setText("Message");
        pay1.setVisible(false);
        labelPrice.setVisible(false);

        labelRPT.setVisible(true);
        labelSurfboards.setVisible(true);
        switch (Wizard.BOOKINGINFO.getOrderType())
        {
            case BOOKING:
                summaryTitle.setText("Summary of your order.");
                summaryDetails.setText("");
                pay1.setVisible(true);
                labelPrice.setVisible(true);
                break;
            case SHARE:
                summaryTitle.setText("Summary of your share request.");
                summaryDetails.setText("These details will be sent to the person who booked the taxi and they will contact you directly. This is *NOT* a taxi booking.");
                labelInterestedSharing.setVisible(false);
                labelInterestedSharingField.setVisible(false);
                labelRequirementsField.setText("Message");
                break;
            case SHARE_ANNOUNCEMENT:
                summaryTitle.setText("Summary of your share announcement.");
                summaryDetails.setText("Your share announcement will be listed. This is *NOT* a taxi booking.");
                labelInterestedSharing.setVisible(false);
                labelInterestedSharingField.setVisible(false);
                labelRequirementsField.setText("Message");
                labelRPT.setVisible(false);
                labelSurfboards.setVisible(false);
                break;
            default:
                break;

        }

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
}
