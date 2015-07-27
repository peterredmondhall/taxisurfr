package com.taxisurfr.client.steps.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.GwtWizard;
import com.taxisurfr.client.core.Wizard;

public class RatingStepUi extends Composite
{
    private static RatingStepUiUiBinder uiBinder = GWT.create(RatingStepUiUiBinder.class);

    interface RatingStepUiUiBinder extends UiBinder<Widget, RatingStepUi>
    {
    }

    @UiField
    Panel mainPanel, hp2;

    @UiField
    Label instruction;

    public RatingStepUi()
    {

        initWidget(uiBinder.createAndBindUi(this));
        setVisible(true);
        hp2.add(getRatingTable());
        // TODO contractor name
        instruction.setText("Please rate your transfer");
    }

    @Override
    public void setVisible(boolean visible)
    {
        mainPanel.setVisible(visible);
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

    public void show(boolean visible, Button prev)
    {
        prev.setVisible(false);
    }

    RadioBar cleanliness = new RadioBar("cleanliness");
    RadioBar safety = new RadioBar("safety");
    RadioBar punctuality = new RadioBar("punctuality");
    RadioBar professionality = new RadioBar("professionality");

    Label labelCleanliness = new Label("select a rating");

    Label labelSafety = new Label("select a rating");
    Label labelPunctuality = new Label("select a rating");
    Label labelProfessionality = new Label("select a rating");

    TextArea critic = new TextArea();
    Label l = new Label("Thanks for your feedback.");

    TextBox nicknameField = new TextBox();

    private DecoratorPanel getRatingTable()
    {
        FlexTable layout = new FlexTable();
        layout.setCellSpacing(6);
        layout.setWidth("300px");
        FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

        // Add a title to the form
        cellFormatter.setColSpan(0, 0, 3);
        cellFormatter.setHorizontalAlignment(
                0, 0, HasHorizontalAlignment.ALIGN_CENTER);

        layout.setWidget(1, 2, new Label("1=poor  5=excellent"));

        layout.setWidget(2, 1, new Label("Cleanliness"));
        layout.setWidget(3, 1, new Label("Safety"));
        layout.setWidget(4, 1, new Label("Punctuality"));
        layout.setWidget(5, 1, new Label("Professionality"));

        layout.setWidget(2, 2, cleanliness.getPanel());
        layout.setWidget(3, 2, safety.getPanel());
        layout.setWidget(4, 2, punctuality.getPanel());
        layout.setWidget(5, 2, professionality.getPanel());

        layout.setWidget(2, 3, labelCleanliness);
        layout.setWidget(3, 3, labelSafety);
        layout.setWidget(4, 3, labelPunctuality);
        layout.setWidget(5, 3, labelProfessionality);
        labelCleanliness.setWidth("100px");
        labelSafety.setWidth("100px");
        labelPunctuality.setWidth("100px");
        labelProfessionality.setWidth("100px");

        hideLabels();

        critic.setWidth("500px");
        critic.setHeight("30px");

        layout.setWidget(6, 1, new Label("How was your experience in words?"));
        layout.setWidget(6, 2, critic);
        layout.setWidget(7, 1, new Label("Your nickname"));
        layout.setWidget(7, 2, nicknameField);
        layout.setWidget(8, 2, getSubmitButton());
        l.setVisible(false);
        layout.setWidget(9, 2, l);

        // Wrap the contents in a DecoratorPanel
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.setWidget(layout);
        return decPanel;
    }

    private void hideLabels()
    {
        labelCleanliness.setVisible(false);
        labelSafety.setVisible(false);
        labelPunctuality.setVisible(false);
        labelProfessionality.setVisible(false);
    }

    private Button getSubmitButton()
    {
        final Button button = new Button("Send Feedback");
        button.setStyleName("btn btn-primary");
        button.setSize("120px", "30px");

        button.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                hideLabels();
                if (cleanliness.getSelection() == -1)
                {
                    labelCleanliness.setVisible(true);
                    return;
                }
                if (safety.getSelection() == -1)
                {
                    labelSafety.setVisible(true);
                    return;
                }
                if (professionality.getSelection() == -1)
                {
                    labelProfessionality.setVisible(true);
                    return;
                }
                if (punctuality.getSelection() == -1)
                {
                    labelPunctuality.setVisible(true);
                    return;
                }
                Wizard.RATINGINFO.setCleanliness(cleanliness.getSelection());
                Wizard.RATINGINFO.setProfessionality(professionality.getSelection());
                Wizard.RATINGINFO.setPunctuality(punctuality.getSelection());
                Wizard.RATINGINFO.setSafety(safety.getSelection());
                Wizard.RATINGINFO.setCritic(critic.getText());
                Wizard.RATINGINFO.setAuthor(nicknameField.getText());

                GwtWizard.SERVICE.addRating(Wizard.RATINGINFO, new AsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(Void xxx)
                    {
                        button.setVisible(false);
                        l.setVisible(true);
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                    }
                });

            }
        });

        return button;
    }

    class RadioBar
    {
        RadioButton rb1;
        RadioButton rb2;
        RadioButton rb3;
        RadioButton rb4;
        RadioButton rb5;
        FlowPanel panel = new FlowPanel();

        RadioBar(String group)
        {
            rb1 = new RadioButton(group, "1");
            rb2 = new RadioButton(group, " ");
            rb3 = new RadioButton(group, " ");
            rb4 = new RadioButton(group, " ");
            rb5 = new RadioButton(group, "5");

            panel.add(rb1);
            panel.add(rb2);
            panel.add(rb3);
            panel.add(rb4);
            panel.add(rb5);
        }

        Panel getPanel()
        {
            return panel;
        }

        int getSelection()
        {
            if (rb1.getValue())
                return 1;
            if (rb2.getValue())
                return 2;
            if (rb3.getValue())
                return 3;
            if (rb4.getValue())
                return 4;
            if (rb5.getValue())
                return 5;
            return -1;
        }

    }

}
