package com.taxisurfr.client.steps.ui.creditcard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.steps.CreditCardStep;
import com.taxisurfr.shared.CurrencyHelper;

public class CreditCardStepUi extends Composite
{
    private static final String PE1 = "Pay now to book the taxi.";
    private static final String PE2 = "Cancel anytime up to 24hrs beforehand for a full refund.";

    public enum ErrorMsg
    {
        NUMBER,
        NAME,
        CVC
    };

    private static ContactStepUiUiBinder uiBinder = GWT.create(ContactStepUiUiBinder.class);

    interface ContactStepUiUiBinder extends UiBinder<Widget, CreditCardStepUi>
    {
    }

    @UiField
    HTMLPanel mainPanel;

    @UiField
    TextBox ccName, ccCvc, ccNumber;

    @UiField
    ListBox ccExpiryMonth, ccExpiryYear;

    @UiField
    Label nameErrorMsg, cvcErrorMsg, numberErrorMsg;

    @UiField
    Label paymentExplination1, paymentExplination2;

    @UiField
    Button payWithCC;

    @UiField
    Panel progressBarPanel;

    Label progress = new Label("Credit card transaction in progress...");
    Label notAccepted = new Label("Credit card was not accepted. Try another?");
    Label refusal = new Label("");

    Button previousButton;

    public CreditCardStepUi(final CreditCardStep step)
    {
        createUi();

        mainPanel.getElement().getStyle().setDisplay(Display.NONE);
        paymentExplination1.setText(PE1);
        paymentExplination2.setText(PE2);

        for (int i = 1; i <= 12; i++)
        {
            ccExpiryMonth.addItem("" + i);
        }
        for (int i = 2015; i < 2025; i++)
        {
            ccExpiryYear.addItem("" + i);
        }
        payWithCC.setVisible(false);
        payWithCC.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                step.pay();
            }
        });

        progressBarPanel.add(progress);
        progressBarPanel.add(notAccepted);
        progressBarPanel.add(refusal);
        progress.setVisible(false);
        notAccepted.setVisible(false);
        notAccepted.setStyleName("errMsg");
        refusal.setVisible(false);
        refusal.setStyleName("errMsg");
    }

    protected void createUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void showPayButton()
    {
        payWithCC.setVisible(true);
    }

    public void showProgress()
    {
//        final ProgressBar progressBar = new ProgressBar(20
//                , ProgressBar.SHOW_TIME_REMAINING
//                        + ProgressBar.SHOW_TEXT);
//        progressBar.setText("Doing something...");
//        progressBarPanel.add(progressBar);
//        Timer t = new Timer()
//        {
//            @Override
//            public void run()
//            {
//                int progress = progressBar.getProgress() + 4;
//                if (progress > 100)
//                    cancel();
//                progressBar.setProgress(progress);
//            }
//        };
//        t.scheduleRepeating(1000);
        progress.setVisible(true);
        notAccepted.setVisible(false);
        refusal.setVisible(false);
        previousButton.setEnabled(false);

    }

    public String getCCNumber()
    {
        return ccNumber.getValue();
    }

    public String getCCName()
    {
        return ccName.getValue();
    }

    public String getCvc()
    {
        return ccCvc.getValue();
    }

    public Integer getCCExpiryMonth()
    {
        return Integer.parseInt(ccExpiryMonth.getValue(ccExpiryMonth.getSelectedIndex()));
    }

    public Integer getCCExpiryYear()
    {
        return Integer.parseInt(ccExpiryYear.getValue(ccExpiryYear.getSelectedIndex()));
    }

    public void setErrorMsg(String msg, ErrorMsg errorMsg)
    {
        switch (errorMsg)
        {
            case NUMBER:
                numberErrorMsg.setText(msg);
                break;
            case NAME:
                nameErrorMsg.setText(msg);
                break;
            case CVC:
                cvcErrorMsg.setText(msg);
                break;
        }
    }

    public void show(boolean visible, Button prev)
    {
        previousButton = prev;
        mainPanel.setVisible(visible);
        mainPanel.getElement().getStyle().setDisplay(visible ? Display.BLOCK : Display.NONE);

        previousButton.setEnabled(true);

        payWithCC.setText("Pay " + CurrencyHelper.getPrice(Wizard.ROUTEINFO, Wizard.getBookingInfo().getCurrency(), Wizard.getBookingInfo().getRate()));
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

    public void setCCNotAccepted()
    {
        progress.setVisible(false);
        notAccepted.setVisible(true);
        refusal.setVisible(false);
        previousButton.setEnabled(true);
    }

    public void setCCNotRefused(String refusalReason)
    {
        progress.setVisible(false);
        notAccepted.setVisible(false);
        refusal.setVisible(true);
        refusal.setText(refusalReason);
    }

}
