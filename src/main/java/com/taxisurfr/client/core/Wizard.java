package com.taxisurfr.client.core;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.steps.ConfirmationStep;
import com.taxisurfr.client.steps.CreditCardStep;
import com.taxisurfr.client.steps.RatingStep;
import com.taxisurfr.client.steps.ShareConfirmationStep;
import com.taxisurfr.client.steps.ShareStep;
import com.taxisurfr.client.steps.TransportStep;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.OrderType;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ProfilInfo;
import com.taxisurfr.shared.model.RatingInfo;
import com.taxisurfr.shared.model.RouteInfo;

public class Wizard extends Composite
{

    private static WizardUiBinder uiBinder = GWT.create(WizardUiBinder.class);

    interface WizardUiBinder extends UiBinder<Widget, Wizard>
    {
    }

    public static BookingInfo BOOKINGINFO;
    public static List<BookingInfo> EXISTING_BOOKINGS_ON_ROUTE;
    public static RouteInfo ROUTEINFO;
    public static ProfilInfo PROFILINFO;
    public static RatingInfo RATINGINFO;
    public static boolean MOBILE = false;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    private final List<WizardStep> stepList;
    private final Map<WizardStep, HTML> headers = Maps.newHashMap();
    private int currentstep;
    private WizardStep initstep;
    private boolean currencyResolved;

    @UiField
    HTMLPanel mainPanel;
    @UiField
    HTMLPanel steps;
    @UiField
    FlowPanel header;
    @UiField
    HTML progressBar;

    @UiField
    Button prev;

    public Wizard()
    {
        stepList = Lists.newArrayList();
        initWidget();
        steps.clear();
        header.clear();
        mainPanel.setVisible(false);
        currentstep = 0;
        HTML html = new HTML("<div class=\"fb-like\" data-href=\"https://www.facebook.com/taxisurfr\" data-layout=\"standard\" data-action=\"like\" data-show-faces=\"true\" data-share=\"true\"></div>");
        mainPanel.add(html);

    }

    protected void initWidget()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void add(WizardStep step)
    {
        HTML headerHTML = new HTML((stepList.size() + 1) + ". " + step.getCaption());
        headers.put(step, headerHTML);
        header.add(headerHTML);

        step.getContent().setVisible(false);
        steps.add(step.getContent());

        stepList.add(step);
    }

    @Override
    protected void onLoad()
    {
        showSocialButtons();
        super.onLoad();
    }

    private static native String showSocialButtons() /*-{
		$wnd.FB.XFBML.parse(); //Render facebook button
    }-*/;

    @Override
    public void setHeight(String height)
    {
        mainPanel.setHeight(height);
    }

    @Override
    public void setWidth(String width)
    {
        mainPanel.setWidth(width);
    }

    @UiHandler("prev")
    public void onPrevClick(ClickEvent event)
    {
        stepList.get(currentstep).getContent().setVisible(false);
        stepList.get(currentstep).onBack();

        currentstep--;

        handlePreviousStep();

        stepList.get(currentstep).getContent().setVisible(true);
        stepList.get(currentstep).show(true, prev);
        updateHeader(currentstep);
    }

    public void onNextClick(ClickEvent event)
    {
        // validation, don't move forward if there are any error on current step
        if (!stepList.get(currentstep).onNext())
        {
            return;
        }

        stepList.get(currentstep).getContent().setVisible(false);

        currentstep++;

        handleNextStep();

        stepList.get(currentstep).getContent().setVisible(true);
        stepList.get(currentstep).show(true, prev);
        updateHeader(currentstep);

    }

    public void stepOver()
    {
        currentstep++;
    }

    private void handleNextStep()
    {
        if (stepList.get(currentstep) instanceof ShareStep)
        {
            if ((BOOKINGINFO != null && BOOKINGINFO.getOrderType() == OrderType.SHARE_ANNOUNCEMENT))
            {
                currentstep++;
            }
        }
        if (stepList.get(currentstep) instanceof CreditCardStep)
        {
            if ((BOOKINGINFO != null && BOOKINGINFO.getOrderType() == OrderType.SHARE_ANNOUNCEMENT))
            {
                BOOKINGINFO.setStatus(OrderStatus.PAID);
                currentstep++;
            }
        }
        if (stepList.get(currentstep) instanceof CreditCardStep)
        {
            if (OrderType.SHARE.equals(BOOKINGINFO.getOrderType()))
            {
                currentstep++;
            }
        }

    }

    private void handlePreviousStep()
    {
        if (stepList.get(currentstep) instanceof ShareStep)
        {
            if (EXISTING_BOOKINGS_ON_ROUTE.size() == 0)
            {
                currentstep--;
            }
        }
    }

    private void updateHeader(int current)
    {
        for (int i = 0; i < stepList.size(); i++)
        {
            HTML headerHTML = headers.get(stepList.get(i));
            if (i == current)
            {
                headerHTML.addStyleName("header-active");
                headerHTML.removeStyleName("header-disable");
            }
            else
            {
                headerHTML.addStyleName("header-disable");
                headerHTML.removeStyleName("header-active");
            }
        }

        // show progress bar
        double per = (current + 1) * 100 / stepList.size();
        progressBar.setWidth(Math.round(per) + "%");
    }

    public void init()
    {
        stepList.get(currentstep).getContent().setVisible(true);

        updateHeader(0);

        mainPanel.setVisible(true);
        if (initstep instanceof TransportStep)
        {
            ((TransportStep) initstep).init(prev);
        }
        if (initstep instanceof ConfirmationStep)
        {
            ((ConfirmationStep) initstep).init(prev);
        }
        if (initstep instanceof RatingStep)
        {
            ((RatingStep) initstep).init(prev);
        }

    }

    public void setInitialStep(WizardStep initstep)
    {
        this.initstep = initstep;

    }

    public void activateShareConfirmationStep(ShareConfirmationStep step)
    {
        step.init(prev);
    }

    public static boolean shareAvailable()
    {
        return EXISTING_BOOKINGS_ON_ROUTE != null && EXISTING_BOOKINGS_ON_ROUTE.size() != 0;
    }

    public boolean getCurrencyResolved()
    {
        return currencyResolved;
    }

    public void setCurrencyResolved(boolean currencyResolved)
    {
        this.currencyResolved = currencyResolved;
    }

}
