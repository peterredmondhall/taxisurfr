package com.taxisurfr.client.core;

import java.util.HashMap;
import java.util.Map;

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
import com.taxisurfr.client.ICallback;

public class PartyRouteWizard extends Composite
{

    private static WizardUiBinder uiBinder = GWT.create(WizardUiBinder.class);

    interface WizardUiBinder extends UiBinder<Widget, PartyRouteWizard>
    {
    }

    private ICallback finishCb;

    private final Map<WizardStep, Integer> map = new HashMap<WizardStep, Integer>();
    private final Map<HTML, Integer> headers = new HashMap<HTML, Integer>();
    private WizardStep currentstep = null;

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
    @UiField
    Button next;
    @UiField
    Button cancel;

    public PartyRouteWizard()
    {
        map.clear();
        initWidget(uiBinder.createAndBindUi(this));
        steps.clear();
        header.clear();
        mainPanel.setVisible(false);
        next.ensureDebugId("button_next");
    }

    public void add(WizardStep step)
    {
        HTML headerHTML = new HTML((headers.size() + 1) + ". " + step.getCaption());
        headers.put(headerHTML, headers.size() + 1);
        header.add(headerHTML);

        step.getContent().setVisible(false);
        steps.add(step.getContent());

        if (!map.containsKey(step))
            map.put(step, map.size() + 1);
    }

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
        int current = map.get(currentstep);
        currentstep.getContent().setVisible(false);

        current -= 1;
        currentstep = getStep(current);
        updateButtons();

        currentstep.getContent().setVisible(true);
        updateHeader(current);
    }

    @UiHandler("next")
    public void onNextClick(ClickEvent event)
    {
        // validation, don't move forward if there are any error on current step
        if (!currentstep.onNext())
        {
            return;
        }

        int current = map.get(currentstep);
        currentstep.getContent().setVisible(false);

        if (current == (map.size() - 1))
        {    // finished
            if (finishCb != null)
                finishCb.execute();

            current += 1;

            // clear all
            //            clearAll();
            //            current = 1;

        }
        else
        {
            current += 1;
        }

        currentstep = getStep(current);

        updateButtons();
        currentstep.getContent().setVisible(true);
        currentstep.show(true, prev);
        updateHeader(current);
    }

    @UiHandler("cancel")
    public void onCancelClick(ClickEvent event)
    {
        // just move to step 1
        currentstep.getContent().setVisible(false);
        currentstep = getStep(1);    // get first step
        updateButtons();

        currentstep.getContent().setVisible(true);
        updateHeader(1);
        prev.setVisible(true);
        next.setVisible(true);
        next.setText("New");
        cancel.setText("Cancel");
    }

    private WizardStep getStep(int stepNo)
    {
        for (WizardStep step : map.keySet())
        {
            if (map.get(step).intValue() == stepNo)
            {
                return step;
            }
        }

        return null;
    }

    private void updateButtons()
    {
        int current = map.get(currentstep);
        prev.setEnabled(current != 1);
        if (current == (map.size() - 1))
        {
            next.setText("Next");
        }
        else if (current == map.size())
        {
            next.setVisible(false);
            prev.setVisible(false);
            cancel.setVisible(false);
        }
        else
        {
            next.setText("Next");
        }
    }

    private void updateHeader(int current)
    {

        for (HTML headerHTML : headers.keySet())
        {
            if (headers.get(headerHTML).intValue() == current)
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
        current = current * 100;
        double per = current / map.size();
        progressBar.setWidth(Math.round(per) + "%");
    }

    private void clearAll()
    {
        for (WizardStep step : map.keySet())
        {
            step.clear();
        }
    }

    /**
     * must called to show wizard
     */
    @Override
    public Composite getWidget()
    {
        for (WizardStep step : map.keySet())
        {
            if (map.get(step) == 1)
            {
                currentstep = step;
                updateButtons();
                currentstep.getContent().setVisible(true);

                updateHeader(1);

                mainPanel.setVisible(true);
                break;
            }
        }
        return this;
    }

    public void addFinishCallback(ICallback cb)
    {
        this.finishCb = cb;
    }
}
