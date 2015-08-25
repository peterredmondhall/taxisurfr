package com.taxisurfr.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.core.WizardMobile;
import com.taxisurfr.client.core.WizardStep;
import com.taxisurfr.client.resources.ClientMessages;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.client.steps.*;
import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.model.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TaxisurfrEntryPoint implements EntryPoint
{
    public static final Logger logger = Logger.getLogger(TaxisurfrEntryPoint.class.getName());
    public static final BookingServiceAsync SERVICE = GWT.create(BookingService.class);
    public static ClientMessages MESSAGES = GWT.create(ClientMessages.class);

    private TransportStep transportStep;
    private ShareStep shareStep;
    private ContactStep contactStep;
    private CreditCardStep creditCardStep;
    private SummaryStep summaryStep;
    private ConfirmationStep confirmationStep;
    private ShareConfirmationStep shareConfirmationStep;
    private RatingStep ratingStep;

    public static final int TRANSPORT = 1;
    public static final int SHARE = 2;
    public static final int CONTACT = 3;
    public static final int SUMMARY = 4;
    public static final int CREDITCARD = 5;
    public static final int CONFIRMATION = 6;
    private static String sessionID = Double.toString(Double.doubleToLongBits(Math.random()));
    private Wizard wizard;

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad()
    {
        Window.setTitle("taxisurfr");
        Wizard.SCREEN_WIDTH = Window.getClientWidth();
        Wizard.SCREEN_HEIGHT = Window.getClientHeight();
        Wizard.MOBILE = Wizard.SCREEN_WIDTH < 500;

        if (Wizard.MOBILE)
        {
            wizard = new WizardMobile();
        }
        else
        {
            wizard = new Wizard();

        }
        continueLoad();
        collectStats(Window.Location.getParameter("src"), Window.Location.getParameter("curr"));

    }

    private void continueLoad()
    {

        transportStep = new TransportStep(wizard);
        shareStep = new ShareStep(wizard);
        contactStep = new ContactStep(wizard);
        creditCardStep = new CreditCardStep(wizard);
        summaryStep = new SummaryStep(wizard);
        confirmationStep = new ConfirmationStep();
        shareConfirmationStep = new ShareConfirmationStep();
        ratingStep = new RatingStep();

        String shareId = Window.Location.getParameter("share");
        String review = Window.Location.getParameter("review");
        String nick = Window.Location.getParameter("nick");
        String routeId = Window.Location.getParameter("route");

        if (review != null)
        {
            Wizard.RATINGINFO = new RatingInfo();
            Wizard.RATINGINFO.setBookingId(Long.parseLong(review));
            Wizard.RATINGINFO.setAuthor(nick);

            List<WizardStep> l = ImmutableList.of((WizardStep) ratingStep);
            completeSetup(ratingStep, l);
            return;
        }
        if (shareId != null)
        {
            handleShareAccepted(Long.parseLong(shareId));
        }
        else
        {
            List<WizardStep> l = ImmutableList.of(transportStep, shareStep, contactStep, summaryStep, creditCardStep, confirmationStep);
            completeSetup(transportStep, l);
            displayRoute(transportStep, routeId);
        }
    }
    public static interface StatInfoMapper extends ObjectMapper<StatInfo>
    {}
    private void collectStats(String src, String currency)
    {
        String protocol = Window.Location.getProtocol();
        String url = protocol + "//" + Window.Location.getHost() + "/stat?src=" + src + "&session=" + sessionID + "&curr=" + currency;
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

        try
        {
            Request response = builder.sendRequest(null, new RequestCallback()
            {
                @Override
                public void onError(Request request, Throwable exception)
                {
                }

                @Override
                public void onResponseReceived(Request request, Response response)
                {
                    try
                    {
                        StatInfoMapper mapper = GWT.create(StatInfoMapper.class);
                        Wizard.STATINFO = mapper.read(response.getText());
                        Currency currency1 = Wizard.STATINFO.getCurrency();
                        logger.log(Level.SEVERE, "XXXXXXXXXXXXXXXX"+ currency1.name());
                        logger.log(Level.SEVERE, "XXXXXXXXXXXXXXXX"+ Wizard.STATINFO.getCurrencyRate());


                        //                        String currencyRate = response.getText();
//                        logger.log(Level.INFO, "currencyRate:" + currencyRate);
//                        List<String> on = Splitter.on("/").splitToList(currencyRate);
//                        Currency currency = Currency.valueOf(on.get(0));
//                        Float rate = 1f;
//                        try
//                        {
//                            rate = Float.parseFloat(on.get(1));
//                        }
//                        catch (Exception ex)
//                        {
//                            rate = 1f;
//                            currency = Currency.USD;
//                        }
                        Wizard.BOOKINGINFO = new BookingInfo();
                        Wizard.BOOKINGINFO.setCurrency(currency1);
                        Wizard.BOOKINGINFO.setRate(Wizard.STATINFO.getCurrencyRate());
                        logger.log(Level.INFO, "after getCurrencyRate currency=" + Wizard.BOOKINGINFO.getCurrency() + " rate=" + Wizard.BOOKINGINFO.getRate());
                        wizard.setCurrencyResolved(true);
                    }
                    catch (Exception ex)
                    {
                        logger.log(Level.SEVERE, "onResponseReceived", ex);
                    }

                }
            });
        }
        catch (RequestException e)
        {
        }

//        
//
//        X-AppEngine-Region
//
//        Name of region from which the request originated. This value only makes sense in the context of the country in X-AppEngine-Country. For example, if the country is "US" and the region is "ca", that "ca" means "California", not Canada.
//        X-AppEngine-City
//
//        Name of the city from which the request originated. For example, a request from the city of Mountain View might have the header value mountain view.
//        X-AppEngine-CityLatLong        
    }

    private void handleShareAccepted(Long shareId)
    {
        SERVICE.handleShareAccepted(shareId, new AsyncCallback<List<BookingInfo>>()
        {

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }

            @Override
            public void onSuccess(List<BookingInfo> sharedBookingList)
            {
                // sharers = sharedBookingList;
                Wizard.BOOKINGINFO = sharedBookingList.get(0);
                shareConfirmationStep.setBookingInfo(sharedBookingList);
                completeSetup(shareConfirmationStep, ImmutableList.of((WizardStep) shareConfirmationStep));
                wizard.activateShareConfirmationStep(shareConfirmationStep);

            }
        });

    }

    private void completeSetup(WizardStep initstep, List<WizardStep> steps)
    {

        wizard.setInitialStep(initstep);
        for (WizardStep step : steps)
        {
            wizard.add(step);
        }
//        wizard.setHeight(height);
//        wizard.setWidth(width);

        wizard.init();
        RootPanel.get().add(wizard);

        SERVICE.getPaypalProfil(new AsyncCallback<ProfilInfo>()
        {

            @Override
            public void onSuccess(ProfilInfo profil)
            {
                Wizard.PROFILINFO = profil;
            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });
    }

    // private List<BookingInfo> sharers;
    public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
    }-*/;

    public static void sendStat(String detail, StatInfo.Update update)
    {
        Wizard.STATINFO.setUpdate(update);
        SERVICE.sendStat(Wizard.STATINFO, new AsyncCallback<Void>()
        {

            @Override
            public void onSuccess(Void profil)
            {
            }

            @Override
            public void onFailure(Throwable caught)
            {
            }
        });

    }

    private void displayRoute(final TransportStep transportStep, String routeId)
    {
        if (routeId == null)
        {
            return;
        }
        try
        {
            Long id = Long.parseLong(routeId);
            SERVICE.getRoute(id, new AsyncCallback<RouteInfo>()
            {

                @Override
                public void onSuccess(RouteInfo routeInfo)
                {
                    Wizard.ROUTEINFO = routeInfo;
                    transportStep.displayRoute();
                }

                @Override
                public void onFailure(Throwable caught)
                {
                }
            });
        }
        catch (Exception ex)
        {

        }
    }
}
