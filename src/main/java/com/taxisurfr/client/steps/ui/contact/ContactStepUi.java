package com.taxisurfr.client.steps.ui.contact;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.shared.CurrencyHelper;
import com.taxisurfr.shared.OrderType;

import java.util.Date;

public class ContactStepUi extends Composite
{
    public enum ErrorMsg
    {
        DATE,
        FIRST_NAME,
        LAST_NAME,
        FLIGHTNO,
        EMAIL,
        EMAIL2,
        ARRIVAL
    }

    ;

    private static ContactStepUiUiBinder uiBinder = GWT.create(ContactStepUiUiBinder.class);

    interface ContactStepUiUiBinder extends UiBinder<Widget, ContactStepUi>
    {
    }

    // boolean test = false;
    @UiField
    HTMLPanel mainPanel;

    @UiField
    DateBox dateBox;

    @UiField
    ListBox pax, surfboards;

    @UiField
    Label dateMsg, dateErrorMsg, flightErrorMsg, firstNameErrorMsg, lastNameErrorMsg, emailErrorMsg, email2ErrorMsg, arrivalErrorMsg, labelWanttoShare;

    @UiField
    Label labelSharing1, labelSharing2, labelBooking, labelFlightLandingTime, labelFlightNo, firstNameMsg, lastNameMsg, labelEmailMsg, labelEmail2Msg;

    @UiField
    Label labelRequirementsField, surfboardsLabel;

    @UiField
    TextBox flightLandingTime, flightNo, firstName, lastName, email, email2;

    @UiField
    CheckBox checkboxWanttoShare;

    @UiField
    TextArea requirementsBox;

    @UiField
    Button nextButtonContact;

    public ContactStepUi(final Wizard wizard)
    {
        createUi();

        mainPanel.getElement().getStyle().setDisplay(Display.NONE);
        for (int i = 1; i < 20; i++)
        {
            pax.addItem("" + i);
        }
        for (int i = 0; i < 20; i++)
        {
            surfboards.addItem("" + i);
        }
        dateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
        restrictDates();
        checkboxWanttoShare.setValue(true);

        //        if (test)
        //        {
        //            dateBox.setValue(new Date());
        //            flightLandingTime.setValue("12:00");
        //            flightNo.setText("MH111");
        //            firstName.setText("Peter");
        //            lastName.setText("Hall");
        //            email.setText("info@taxigang.com");
        //            email2.setText("info@taxigang.com");
        //
        //        }
        nextButtonContact.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                wizard.onNextClick(null);
            }
        });
    }

    private void restrictDates()
    {
        dateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
        {
            @Override
            public void onValueChange(final ValueChangeEvent<Date> dateValueChangeEvent)
            {
                if (dateValueChangeEvent.getValue().before(earliest()))
                {
                    dateBox.setValue(earliest(), false);
                }
            }
        });
        dateBox.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>()
        {
            @Override
            public void onShowRange(final ShowRangeEvent<Date> dateShowRangeEvent)
            {
                final Date today = earliest();
                Date d = zeroTime(dateShowRangeEvent.getStart());
                while (d.before(today))
                {
                    dateBox.getDatePicker().setTransientEnabledOnDates(false, d);
                    nextDay(d);
                }
            }
        });
    }

    private Date earliest()
    {
        Date earliest = new Date();
        nextDay(earliest);
        //nextDay(earliest);
        return zeroTime(earliest);
    }

    /**
     * this is important to get rid of the time portion, including ms
     */
    private Date zeroTime(final Date date)
    {
        Date zeroTimeDate = DateTimeFormat.getFormat("yyyyMMdd").parse(DateTimeFormat.getFormat("yyyyMMdd").format(date));
        zeroTimeDate.setHours(12);
        return zeroTimeDate;
    }

    private static void nextDay(final Date date)
    {
        com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(date, 1);
    }

    protected void createUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Date getDate()
    {
        Date date = dateBox.getValue();
        if (date != null)
        {
            date = zeroTime(date);
        }
        return date;
    }

    public String getFirstName()
    {
        return firstName.getValue();
    }

    public String getLastName()
    {
        return lastName.getValue();
    }

    public String getEmail()
    {
        return email.getValue();
    }

    public String getEmail2()
    {
        return email2.getValue();
    }

    public String getArrivalTime()
    {
        return flightLandingTime.getText();
    }

    public String getSurfboards()
    {
        return surfboards.getItemText(surfboards.getSelectedIndex());
    }

    public String getPax()
    {
        return pax.getItemText(pax.getSelectedIndex());
    }

    public String getFlightNo()
    {
        return flightNo.getValue();
    }

    public boolean getWantToShare()
    {
        return checkboxWanttoShare.getValue();
    }

    public String getRequirements()
    {
        String requirements = requirementsBox.getText();
        if (requirements == null)
        {
            requirements = "none";
        }
        return requirements;
    }

    public void setErrorMsg(String msg, ErrorMsg errorMsg)
    {
        switch (errorMsg)
        {
            case DATE:
                dateErrorMsg.setText(msg);
                break;
            case FIRST_NAME:
                firstNameErrorMsg.setText(msg);
                break;
            case LAST_NAME:
                lastNameErrorMsg.setText(msg);
                break;
            case EMAIL:
                emailErrorMsg.setText(msg);
                break;
            case EMAIL2:
                email2ErrorMsg.setText(msg);
                break;
            case FLIGHTNO:
                flightErrorMsg.setText(msg);
                break;
            case ARRIVAL:
                arrivalErrorMsg.setText(msg);
                break;
        }
    }

    public void show(boolean visible, Button prev)
    {
        resetErrMsg();
        mainPanel.setVisible(visible);
        mainPanel.getElement().getStyle().setDisplay(visible ? Display.BLOCK : Display.NONE);

        prev.setEnabled(true);
        prev.setVisible(true);

        surfboardsLabel.setVisible(true);
        surfboards.setVisible(true);
        labelRequirementsField.setVisible(true);
        requirementsBox.setVisible(true);
        switch (Wizard.BOOKINGINFO.getOrderType())
        {
            case BOOKING:
                checkboxWanttoShare.setVisible(true);
                labelWanttoShare.setVisible(true);

                labelBooking.setVisible(true);
                labelSharing1.setVisible(false);
                labelSharing2.setVisible(false);
                dateBox.setEnabled(true);
                labelRequirementsField.setText("Other requirements");
                break;
            case SHARE:
                checkboxWanttoShare.setVisible(false);
                labelWanttoShare.setVisible(false);

                labelBooking.setVisible(false);
                labelSharing1.setVisible(true);
                labelSharing2.setVisible(true);
                dateBox.setEnabled(false);
                labelRequirementsField.setText("Message to taxi booker eg. Facebook profile etc.");
                labelSharing1.setText("You are creating a share request.");
                labelSharing2.setText("Enter your details. You will be contacted when the fellow passenger has agreed to the share.");
                dateBox.setValue(Wizard.BOOKINGINFO.getDate());
                break;
            case SHARE_ANNOUNCEMENT:
                checkboxWanttoShare.setVisible(false);
                labelWanttoShare.setVisible(false);

                labelBooking.setVisible(false);
                labelSharing1.setVisible(true);
                labelSharing2.setVisible(true);
                surfboardsLabel.setVisible(false);
                surfboards.setVisible(false);
                labelRequirementsField.setVisible(true);
                requirementsBox.setVisible(true);
                labelSharing1.setText("You are creating a sharing announcement.");
                labelSharing2.setText("It will appear in the sharing list on this route.");

                dateBox.setEnabled(true);
                break;
            default:
                break;

        }
        boolean sharing = Wizard.BOOKINGINFO.getOrderType() == OrderType.SHARE;
        if (sharing)
        {
            labelRequirementsField.setText("Message to taxi booker eg. Facebook profile etc.");
            dateBox.setValue(Wizard.BOOKINGINFO.getDate());
        }

        labelFlightNo.setText(Wizard.ROUTEINFO.getPickupType().getLocationType());
        labelFlightLandingTime.setText(Wizard.ROUTEINFO.getPickupType().getTimeType());

        labelBooking.setText(Wizard.ROUTEINFO.getKey(CurrencyHelper.getPrice(Wizard.ROUTEINFO, Wizard.BOOKINGINFO.getCurrency(), Wizard.BOOKINGINFO.getRate())));

    }

    private void resetErrMsg()
    {
        String msg = "";
        dateErrorMsg.setText(msg);
        firstNameErrorMsg.setText(msg);
        lastNameErrorMsg.setText(msg);
        emailErrorMsg.setText(msg);
        email2ErrorMsg.setText(msg);
        flightErrorMsg.setText(msg);
        arrivalErrorMsg.setText(msg);

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
