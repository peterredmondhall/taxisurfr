package com.taxisurfr.client.steps.ui.transport;

import static com.taxisurfr.client.core.Wizard.BOOKINGINFO;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.GwtWizard;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.client.steps.ui.ButtonFactory;
import com.taxisurfr.client.steps.ui.widget.RatingList;
import com.taxisurfr.shared.CurrencyHelper;
import com.taxisurfr.shared.OrderType;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.RatingInfo;
import com.taxisurfr.shared.model.RouteInfo;
import com.taxisurfr.shared.model.StatInfo;

public class TransportStepUi extends Composite
{
    // public static final String WIDTH = "120px";
    public static final Logger logger = Logger.getLogger(TransportStepUi.class.getName());

    private static RouteStepUiUiBinder uiBinder = GWT.create(RouteStepUiUiBinder.class);
    private final BookingServiceAsync service = GWT.create(BookingService.class);

    interface RouteStepUiUiBinder extends UiBinder<Widget, TransportStepUi>
    {
    }

    @UiField
    Panel mainPanel, ratingsPanel, dp, panelMotivation;

    @UiField
    Image imageVehicle, imageSpinner, imageSearch;

    @UiField
    Panel routeSuggestionPanel, panelRoute;

    @UiField
    Label labelRouteName;

    @UiField
    Panel panelDescription;

    private final Map<String, RouteInfo> mapRouteInfo = Maps.newHashMap();
    private final ScrollPanel sp = new ScrollPanel();
    private final FlowPanel fp = new FlowPanel();

//    @UiField
    Button buttonOrder, buttonAnnounce, buttonShare;
    @UiField
    FlexTable buttontable;

    private final Wizard wizard;

    public TransportStepUi(final Wizard wizard)
    {
        this.wizard = wizard;
        createUi();
        fetchRoutes();
        panelRoute.setVisible(false);
        sp.setHeight(getPanelHeight());
        ratingsPanel.add(sp);
        sp.add(fp);
        panelRoute.setHeight(getPanelHeight());

        FlexTable table = new FlexTable();
        table.setWidget(0, 0, getImage());
        table.setWidget(0, 1, new Label("Full refund up to 24 hrs. beforehand"));
        table.setWidget(1, 0, getImage());
        table.setWidget(1, 1, new Label("Trusted driver and a safe vehicle"));
        table.setWidget(2, 0, getImage());
        table.setWidget(2, 1, new Label("Sharing function if required"));
        // containerGrid.setStyleName("progressbar-outer");

        panelMotivation.add(table);
        imageSearch.setVisible(false);

        buttonOrder = ButtonFactory.getButton("Book taxi now.", "150px", "80px");
        buttontable.setWidget(0, 0, buttonOrder);
        buttonOrder.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                wizard.onNextClick(null);
                wizard.onNextClick(null);

            }
        });
    }

    private void createButtonTable()
    {
        ClickHandler shareBook = new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                wizard.onNextClick(null);

            }
        };
        int row = 1;
        for (int i = row; i < buttontable.getRowCount(); i++)
        {
            buttontable.removeRow(i);
        }
        if (Wizard.shareAvailable())
        {
            buttonShare = ButtonFactory.getButton("Share.", "150px");
            buttonShare.addClickHandler(shareBook);
            buttontable.setWidget(row++, 0, buttonShare);
        }
        buttonAnnounce = ButtonFactory.getButton("Announce share.", "150px");
        buttontable.setWidget(row++, 0, buttonAnnounce);

        buttonAnnounce.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                Wizard.BOOKINGINFO.setOrderType(OrderType.SHARE_ANNOUNCEMENT);
                wizard.onNextClick(null);

            }
        });
    }

    private Image getImage()
    {
        Image image = new Image("images/big-tick.jpg");
        String size = "30px";
        image.setSize(size, size);
        return image;

    }

    protected void createUi()
    {
        initWidget(uiBinder.createAndBindUi(this));
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

    private void fetchRoutes()
    {
        service.getRoutes(new AsyncCallback<List<RouteInfo>>()
        {

            @Override
            public void onSuccess(final List<RouteInfo> routes)
            {
                logger.log(Level.INFO, "fetchRoutes count = " + routes.size());
                MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

                for (RouteInfo routeInfo : routes)
                {
                    String key = routeInfo.getKey("");
                    mapRouteInfo.put(key, routeInfo);

                    oracle.add(key);
                }
                final SuggestBox suggestBox = new SuggestBox(oracle);
                setSuggestBoxWidth(suggestBox);
                routeSuggestionPanel.add(suggestBox);
                suggestBox.getElement().setAttribute("placeHolder", "eg. Colombo Airport Arugam Bay");

                SelectionHandler<SuggestOracle.Suggestion> handler = new SelectionHandler<SuggestOracle.Suggestion>()
                {
                    @Override
                    public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event)
                    {
                        String displayString = event.getSelectedItem().getReplacementString();
                        RouteInfo routeInfo = mapRouteInfo.get(displayString);
                        Wizard.ROUTEINFO = routeInfo;
                        displayRoute(/* suggestBox, */);
                    }

                };

                suggestBox.addSelectionHandler(handler);
                imageSpinner.setVisible(false);
                imageSearch.setVisible(true);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                logger.log(Level.SEVERE, "fetching routes");
                Refresh.refresh();
            }
        });
    }

    private Widget getDisclosure(String description)
    {
        int defautlt = description.length() > 70 ? 70 : description.length();
        int breakCount = description.indexOf('.') + 1;
        breakCount = breakCount > 0 ? breakCount : defautlt;

        DisclosurePanel advancedDisclosure = new DisclosurePanel(description.substring(0, breakCount));
        advancedDisclosure.setAnimationEnabled(true);
        Label descriptionLabel = new Label(description.substring(breakCount));
        advancedDisclosure.setContent(descriptionLabel);

        return advancedDisclosure;

    }

    public void displayRoute()
    {
        waitForCurrencyResolved();

    }

    private void waitForCurrencyResolved()
    {
        if (wizard.getCurrencyResolved())
        {
            continueLoading();
        }
        else
        {
            Timer t = new Timer()
            {
                @Override
                public void run()
                {
                    waitForCurrencyResolved();
                }
            };
            t.schedule(500);
        }
    }

    private void continueLoading()
    {
        logger.log(Level.INFO, "routeInfo" + Wizard.ROUTEINFO.getKey(""));
        logger.log(Level.INFO, "currency" + BOOKINGINFO.getCurrency() + "   rate:" + BOOKINGINFO.getRate());
        logger.log(Level.INFO, "routeCents" + Wizard.ROUTEINFO.getCents());

        logger.log(Level.INFO, "paidPrice" + CurrencyHelper.getPriceInDollars(Wizard.ROUTEINFO, BOOKINGINFO.getCurrency(), BOOKINGINFO.getRate()));
        BOOKINGINFO.setPaidPrice(CurrencyHelper.getPriceInDollars(Wizard.ROUTEINFO, BOOKINGINFO.getCurrency(), BOOKINGINFO.getRate()));
        logger.log(Level.INFO, "paidprice=" + BOOKINGINFO.getPaidPrice() + "rate=" + BOOKINGINFO.getRate());

        panelMotivation.setVisible(false);
        RouteInfo routeInfo = Wizard.ROUTEINFO;
        labelRouteName.setText(routeInfo.getKey(CurrencyHelper.getPrice(routeInfo, Wizard.BOOKINGINFO.getCurrency(), Wizard.BOOKINGINFO.getRate())));
        imageVehicle.setUrl("/imageservice?image=" + routeInfo.getImage());
        panelDescription.clear();
        panelDescription.add(getDisclosure(routeInfo.getDescription()));

        panelRoute.setVisible(true);
        GwtWizard.SERVICE.getBookingsForRoute(Wizard.ROUTEINFO, new AsyncCallback<List<BookingInfo>>()
        {
            @Override
            public void onSuccess(List<BookingInfo> list)
            {
                Wizard.EXISTING_BOOKINGS_ON_ROUTE = list;
                createButtonTable();
                // suggestBox.getElement().setAttribute("placeHolder", "Enter a start or destination eg. Colombo or Arugam Bay");
            }

            @Override
            public void onFailure(Throwable caught)
            {
            }
        });

        GwtWizard.SERVICE.getRatings(Wizard.ROUTEINFO, new AsyncCallback<List<RatingInfo>>()
        {
            @Override
            public void onSuccess(List<RatingInfo> ratings)
            {
                if (ratings.size() > 0)
                {
                    fp.add(new RatingList(ratings).createRatingForm());
                }
            }

            @Override
            public void onFailure(Throwable caught)
            {
            }
        });
        GwtWizard.sendStat(routeInfo.getKey(""), StatInfo.Update.ROUTE);
    }

    private void setSuggestBoxWidth(SuggestBox suggestBox)
    {

        int suggestBoxWidth = 700;
        if (Wizard.MOBILE)
        {
            suggestBoxWidth = (int) (Wizard.SCREEN_WIDTH * 0.75);
        }
        suggestBox.setWidth(suggestBoxWidth + "px");

    }

    private String getPanelHeight()
    {
        int panelHeight = 200;
        if (Wizard.MOBILE)
        {
            panelHeight = (int) (Wizard.SCREEN_HEIGHT / 2 * 0.5);
        }

        return panelHeight + "px";
    }
}
