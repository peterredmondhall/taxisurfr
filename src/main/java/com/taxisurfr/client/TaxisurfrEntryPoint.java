package com.taxisurfr.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.github.nmorel.gwtjackson.client.ObjectReader;
import com.github.nmorel.gwtjackson.client.ObjectWriter;
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
import com.taxisurfr.shared.CurrencyRequest;
import com.taxisurfr.shared.model.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private String routeId;
    private String review;
    private String nick;
    private String shareId;

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

        shareId = Window.Location.getParameter("share");
        review = Window.Location.getParameter("review");
        nick = Window.Location.getParameter("nick");
        routeId = Window.Location.getParameter("route");
        logger.info("requested route:" + routeId);

        logger.info("load():");

        if (Wizard.MOBILE)
        {
            wizard = new WizardMobile();
        }
        else
        {
            wizard = new Wizard();

        }
        continueLoad();
        collectStats(Window.Location.getParameter("src"), Window.Location.getParameter("curr"), Window.Location.getParameter("route"));

    }

    private void continueLoad()
    {
        logger.info("continueLoad");
        transportStep = new TransportStep(wizard);
        shareStep = new ShareStep(wizard);
        contactStep = new ContactStep(wizard);
        creditCardStep = new CreditCardStep(wizard);
        summaryStep = new SummaryStep(wizard);
        confirmationStep = new ConfirmationStep();
        shareConfirmationStep = new ShareConfirmationStep();
        ratingStep = new RatingStep();

        if (review != null)
        {
            Wizard.RATINGINFO = new RatingInfo();
            Wizard.RATINGINFO.setBookingId(Long.parseLong(review));
            Wizard.RATINGINFO.setAuthor(nick);

            List<WizardStep> l = ImmutableList.of((WizardStep) ratingStep);
            addSteps(ratingStep, l);
            return;
        }
        if (shareId != null)
        {
            handleShareAccepted(Long.parseLong(shareId));
        }
        else
        {
            List<WizardStep> l = ImmutableList.of(transportStep, shareStep, contactStep, summaryStep, creditCardStep, confirmationStep);
            addSteps(transportStep, l);
        }
    }

    public static void sendStat(String s, StatInfo.Update type)
    {
        //FIXME
    }

    public static interface StatInfoMapper extends ObjectMapper<StatInfo>
    {
    }

    public static interface CurrencyRequestWriter extends ObjectWriter<CurrencyRequest>
    {
    }

    public static interface CurrencyResponseReader extends ObjectReader<StatInfo>
    {
    }

    private static final CurrencyRequestWriter GREETING_REQUEST_WRITER = GWT.create(CurrencyRequestWriter.class);

    private static final CurrencyResponseReader GREETING_RESPONSE_READER = GWT.create(CurrencyResponseReader.class);

    private void collectStats(String src, String currency, final String routeId)
    {
        Wizard.getBookingInfo().setCurrency(Wizard.getStatInfo().getCurrency());
        Wizard.getBookingInfo().setRate(Wizard.getStatInfo().getCurrencyRate());
        logger.log(Level.INFO, "assuming currency=" + Wizard.getBookingInfo().getCurrency() + " rate=" + Wizard.getBookingInfo().getRate());
        final long now = new Date().getTime();
        try

        {
            String protocol = Window.Location.getProtocol();
            String url = protocol + "//" + Window.Location.getHost() + "/stat";
            CurrencyRequest currencyRequest = new CurrencyRequest(src, currency);
            new RequestBuilder(RequestBuilder.POST, url).sendRequest(GREETING_REQUEST_WRITER
                    .write(currencyRequest), new RequestCallback()
            {
                @Override
                public void onResponseReceived(Request request, Response response)
                {
                    Wizard.setStatInfo(GREETING_RESPONSE_READER.read(response.getText()));
                    Wizard.getBookingInfo().setCurrency(Wizard.getStatInfo().getCurrency());
                    Wizard.getBookingInfo().setRate(Wizard.getStatInfo().getCurrencyRate());
                    logger.log(Level.INFO, "after getCurrencyRate currency (attempt 1)=" + Wizard.getBookingInfo().getCurrency() + " rate=" + Wizard.getBookingInfo().getRate());
                    transportStep.setLoaded();
                    displayRoute(transportStep);
                    logger.log(Level.INFO, "time to get currency=" + (new Date().getTime() - now));
                }

                @Override
                public void onError(Request request, Throwable throwable)
                {
                    logger.log(Level.SEVERE, "attempt 1");
                }
            });
        }

        catch (RequestException e)
        {
            logger.log(Level.SEVERE, e.getMessage());
        }

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
                Wizard.setBookingInfo(sharedBookingList.get(0));
                shareConfirmationStep.setBookingInfo(sharedBookingList);
                addSteps(shareConfirmationStep, ImmutableList.of((WizardStep) shareConfirmationStep));
                wizard.activateShareConfirmationStep(shareConfirmationStep);

            }
        });

    }

    private void addSteps(WizardStep initstep, List<WizardStep> steps)
    {
        logger.info("addSteps");

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

    private void displayRoute(final TransportStep transportStep)
    {
        if (routeId == null || "null".equals(routeId))
        {
            logger.info("displayRoute: no route displayed:" + routeId);
            return;
        }
        try
        {
            Long id = Long.parseLong(routeId);
            logger.info("displayRoute: route to be displayed:" + id);
            SERVICE.getRoute(id, new AsyncCallback<RouteInfo>()
            {

                @Override
                public void onSuccess(RouteInfo routeInfo)
                {
                    Wizard.ROUTEINFO = routeInfo;
                    logger.info("display route:" + Wizard.ROUTEINFO.getKey(""));
                    transportStep.displayRoute(routeInfo.getKey(""));
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    logger.log(Level.SEVERE, " couldnt get route with id" + routeId);
                }
            });
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, " couldnt get route with id" + routeId);

        }
    }
}
