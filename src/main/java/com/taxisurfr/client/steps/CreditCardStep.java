package com.taxisurfr.client.steps;

import com.arcbees.stripe.client.CreditCard;
import com.arcbees.stripe.client.CreditCardResponseHandler;
import com.arcbees.stripe.client.StripeFactory;
import com.arcbees.stripe.client.jso.CreditCardResponse;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.client.steps.ui.creditcard.CreditCardStepMobileUi;
import com.taxisurfr.client.steps.ui.creditcard.CreditCardStepUi;
import com.taxisurfr.client.steps.ui.creditcard.CreditCardStepUi.ErrorMsg;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.client.GwtWizard;

public class CreditCardStep implements WizardStep
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);

    private final CreditCardStepUi ui;
    private final Wizard wizard;
    private boolean stripeInjected;

    public CreditCardStep(Wizard wizard)
    {
        if (Wizard.MOBILE)
        {
            ui = new CreditCardStepMobileUi(this);
        }
        else
        {
            ui = new CreditCardStepUi(this);
        }
        this.wizard = wizard;
    }

    @Override
    public String getCaption()
    {
        return Wizard.MOBILE ? "" : "Payment";
    }

    @Override
    public Composite getContent()
    {
        return ui;
    }

    public Boolean pay()
    {

        ui.setErrorMsg("", ErrorMsg.NAME);
        if (ui.getCCName() == null || ui.getCCName().trim().length() == 0)
        {
            ui.setErrorMsg(GwtWizard.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.NAME);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.NUMBER);
        if (ui.getCCNumber() == null || ui.getCCNumber().trim().length() == 0)
        {
            ui.setErrorMsg(GwtWizard.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.NUMBER);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.CVC);
        if (ui.getCvc() == null || ui.getCvc().trim().length() == 0)
        {
            ui.setErrorMsg(GwtWizard.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.CVC);
            return false;
        }
        ui.showProgress();
        payWithStripe();
        return false;
    }

    private void injectStripeJs()
    {

        if (!stripeInjected)
        {
            StripeFactory.get().inject(new Callback<Void, Exception>()
            {
                @Override
                public void onFailure(Exception reason)
                {
                }

                @Override
                public void onSuccess(Void result)
                {
                    stripeInjected = true;
                    injectStripeJs();
                }
            });
        }
        ui.showPayButton();
    }

    void payWithStripe()
    {
        StripeFactory.get().setPublishableKey(Wizard.PROFILINFO.getStripePublishable());

        CreditCard creditCard = new CreditCard.Builder()
                // .creditCardNumber(ui.getCCName())
                .creditCardNumber(ui.getCCNumber())
                // .cvc(ui.getCvc())
                .cvc(ui.getCvc())
                // .expirationMonth(ui.getCCExpiryMonth())
//                .expirationYear(ui.getCCExpiryYear())
                .expirationMonth(ui.getCCExpiryMonth())
                .expirationYear(ui.getCCExpiryYear())
                .name(ui.getCCName())
                // .addressLine1("1093 Charleston rd")
//                .addressLine2("apt. 3")
//                .addressCity("Los Angeles")
//                .addressCountry("United States")
//                .addressState("CA")
//                .addressZip("91257")
                .build();

        StripeFactory.get().getCreditCardToken(creditCard, new CreditCardResponseHandler()
        {
            @Override
            public void onCreditCardReceived(int status, CreditCardResponse creditCardResponse)
            {
                if (creditCardResponse.getId() != null)
                {
                    service.payWithStripe(creditCardResponse.getId(), Wizard.BOOKINGINFO, new AsyncCallback<BookingInfo>()
                    {

                        @Override
                        public void onFailure(Throwable caught)
                        {
                            Window.alert("Failed payment");
                        }

                        @Override
                        public void onSuccess(BookingInfo bi)
                        {
                            if (bi.getStatus().equals(OrderStatus.PAID))
                            {
                                Wizard.BOOKINGINFO.setStatus(bi.getStatus());
                                wizard.onNextClick(null);
                            }
                            else
                            {
                                ui.setCCNotRefused(bi.getStripeRefusalReason());
                            }
                        }
                    });
                }
                else
                {
                    ui.setCCNotAccepted();
                }

            }
        });
    }

    @Override
    public Boolean onBack()
    {
        return true;
    }

    @Override
    public void clear()
    {

    }

    @Override
    public void show(boolean visible, Button prev)
    {
        ui.show(visible, prev);
        injectStripeJs();
    }

    @Override
    public Boolean onNext()
    {
        return true;
    }

}
