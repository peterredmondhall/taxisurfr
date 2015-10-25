package com.taxisurfr.client.steps;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.taxisurfr.client.TaxisurfrEntryPoint;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.steps.ui.contact.ContactStepMobileUi;
import com.taxisurfr.client.steps.ui.contact.ContactStepUi;
import com.taxisurfr.client.steps.ui.contact.ContactStepUi.ErrorMsg;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.StatInfo;

import java.util.logging.Logger;

public class ContactStep implements WizardStep
{
    private static final Logger logger = Logger.getLogger(ContactStep.class.getName());
    private static DateTimeFormat sdf = DateTimeFormat.getFormat("dd.MM.yyyy");

    private final ContactStepUi ui;

    public ContactStep(Wizard wizard)
    {
        if (Wizard.MOBILE)
        {
            ui = new ContactStepMobileUi(wizard);
        }
        else
        {
            ui = new ContactStepUi(wizard);
        }

    }

    @Override
    public String getCaption()
    {
        return Wizard.MOBILE ? "" : "Contact";
    }

    @Override
    public Composite getContent()
    {
        return ui;
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isEmailValid(String email)
    {
        boolean result = false;
        if (email != null)
        {
            result = email.matches(EMAIL_PATTERN);
        }
        return result;
    }

    @Override
    public Boolean onNext()
    {

        ui.setErrorMsg("", ErrorMsg.DATE);
        if (ui.getDate() == null)
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.DATE);
            return false;
        }

        ui.setErrorMsg("", ErrorMsg.FLIGHTNO);
        if (ui.getFlightNo() == null || ui.getFlightNo().trim().length() == 0)
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.FLIGHTNO);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.ARRIVAL);
        if (ui.getArrivalTime() == null || ui.getArrivalTime().trim().length() == 0)
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.ARRIVAL);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.FIRST_NAME);
        if (ui.getFirstName() == null || ui.getFirstName().trim().length() == 0)
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.FIRST_NAME);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.LAST_NAME);
        if (ui.getLastName() == null || ui.getLastName().trim().length() == 0)
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.LAST_NAME);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.EMAIL);
        if (ui.getEmail() == null || !isEmailValid(ui.getEmail()))
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mustBeValidEmailErrorMsg(), ErrorMsg.EMAIL);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.EMAIL2);
        if (ui.getEmail2() == null || !isEmailValid(ui.getEmail2()))
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mustBeValidEmailErrorMsg(), ErrorMsg.EMAIL2);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.EMAIL2);
        if (!ui.getEmail2().equals(ui.getEmail()))
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mustBeEqualEmail(), ErrorMsg.EMAIL2);
            return false;
        }
        ui.setErrorMsg("", ErrorMsg.FLIGHTNO);
        if (ui.getLastName() == null || ui.getLastName().trim().length() == 0)
        {
            ui.setErrorMsg(TaxisurfrEntryPoint.MESSAGES.mayNotBeEmptyErrorMsg(), ErrorMsg.FLIGHTNO);
            return false;
        }

        Wizard.getBookingInfo().setDate(ui.getDate());
        Wizard.getBookingInfo().setDateText(sdf.format(Wizard.getBookingInfo().getDate()));

        Wizard.getBookingInfo().setLandingTime(ui.getArrivalTime());
        Wizard.getBookingInfo().setName(ui.getFirstName() + "  " + ui.getLastName());
        Wizard.getBookingInfo().setEmail(ui.getEmail());

        Wizard.getBookingInfo().setFlightNo(ui.getFlightNo());
        Wizard.getBookingInfo().setPax(Integer.parseInt(ui.getPax()));
        Wizard.getBookingInfo().setSurfboards(Integer.parseInt(ui.getSurfboards()));
        Wizard.getBookingInfo().setShareWanted(ui.getWantToShare());
        Wizard.getBookingInfo().setRequirements(ui.getRequirements());

        Wizard.getBookingInfo().setRouteInfo(Wizard.ROUTEINFO);
        Wizard.getBookingInfo().setRouteId(Wizard.ROUTEINFO.getId());
        TaxisurfrEntryPoint.SERVICE.addBooking(Wizard.getBookingInfo(), new AsyncCallback<BookingInfo>()
        {
            @Override
            public void onSuccess(BookingInfo result)
            {
                Wizard.getBookingInfo().setId(result.getId());
            }

            @Override
            public void onFailure(Throwable caught)
            {
                logger.severe("couldnt add booking");
            }
        });

        TaxisurfrEntryPoint.sendStat("step:Contact", StatInfo.Update.TYPE);
        return true;
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
    }

}
