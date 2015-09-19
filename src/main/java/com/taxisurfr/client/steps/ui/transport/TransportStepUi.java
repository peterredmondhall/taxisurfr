package com.taxisurfr.client.steps.ui.transport;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.TaxisurfrEntryPoint;
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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.taxisurfr.client.core.Wizard.BOOKINGINFO;

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
    Image imageVehicle, imageSearch;

    @UiField
    Panel routeSuggestionPanel, panelRoute;

    @UiField
    Label labelRouteName;

    @UiField
    Panel panelDescription;

    private final Map<String, RouteInfo> mapRouteInfo = Maps.newHashMap();
    private final ScrollPanel sp = new ScrollPanel();
    private final FlowPanel fp = new FlowPanel();
    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    final SuggestBox suggestBox = new SuggestBox(oracle);

    //    @UiField
    Button buttonOrder, buttonAnnounce, buttonShare;

    @UiField
    Panel buttontable;

    VerticalPanel sharingPanel = new VerticalPanel();

    private final Wizard wizard;
    List<RouteInfo> routes = Lists.<RouteInfo>newArrayList();

    public TransportStepUi(final Wizard wizard)
    {
        this.wizard = wizard;
        createUi();
        initOracle();
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

        buttonOrder = ButtonFactory.getButton("Book taxi now.", "150px", "80px");
        buttontable.add(buttonOrder);
        buttonOrder.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                wizard.onNextClick(null);
                wizard.onNextClick(null);
            }
        });
        initSharingPanel();
    }

    private void initSharingPanel()
    {

        //DecoratorPanel decPanel = new DecoratorPanel();
        DisclosurePanel disclosurePanel = new DisclosurePanel("Sharing");
        disclosurePanel.setAnimationEnabled(true);
        disclosurePanel.setContent(sharingPanel);

        buttontable.add(disclosurePanel);
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

    private void initOracle()
    {
        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

        setSuggestBoxWidth(suggestBox);
        routeSuggestionPanel.add(suggestBox);
        suggestBox.getElement().setAttribute("placeHolder", "eg. Colombo Airport Arugam Bay");

        SelectionHandler<SuggestOracle.Suggestion> handler = new SelectionHandler<SuggestOracle.Suggestion>()
        {
            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event)
            {
                String displayString = event.getSelectedItem().getReplacementString();
                logger.log(Level.INFO, "displayString:" + displayString);

                RouteInfo routeInfo = mapRouteInfo.get(displayString);
                logger.log(Level.INFO, "routeInfo:" + routeInfo.getKey(""));

                Wizard.ROUTEINFO = routeInfo;
                logger.log(Level.INFO, "start:" + Wizard.ROUTEINFO.getStart());
                displayRoute(/* suggestBox, */);
            }

        };

        suggestBox.addSelectionHandler(handler);

        suggestBox.addKeyUpHandler(new KeyUpHandler()
        {
            @Override public void onKeyUp(KeyUpEvent keyUpEvent)
            {
                String query = suggestBox.getText();
                switch (query.length())
                {
                    case 0:
                        routes = Lists.<RouteInfo>newArrayList();
                        loadRoutes();
                    case 1:
                    case 2:
                    case 3:
                        fetchRoutes(suggestBox.getText());
                        //                        Timer t = new Timer()
                        //                        {
                        //                            public void run()
                        //                            {
                        //                                fetchRoutes(suggestBox.getText());
                        //                            }
                        //                        };
                        //                        t.schedule(100);
                        break;
                    default:
                        List<RouteInfo> list = Lists.newArrayList();
                        for (RouteInfo routeInfo : routes)
                        {
                            for (String q : Splitter.on(' ').split(query))
                            {
                                q = q.toUpperCase();
                                if (!routeInfo.isInactive() && (routeInfo.getStart().toUpperCase().contains(q) || routeInfo.getEnd().toUpperCase().contains(q)))
                                {
                                    list.add(routeInfo);
                                }
                            }

                        }
                        loadRoutes(list);
                }

            }
        });
    }

    private void fetchRoutes(final String query)
    {
        service.getRoutes(query, new AsyncCallback<List<RouteInfo>>()
        {

            @Override
            public void onSuccess(final List<RouteInfo> routesFromQuery)
            {
                routes = routesFromQuery;
            }

            @Override
            public void onFailure(Throwable caught)
            {
                service.getRoutes(query, new AsyncCallback<List<RouteInfo>>()
                {

                    @Override
                    public void onSuccess(final List<RouteInfo> routesFromQuery)
                    {
                        routes = routesFromQuery;
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        logger.log(Level.SEVERE, "fetching routes");
                        Refresh.refresh();
                    }
                });
            }
        });
    }

    private void loadRoutes()
    {
        loadRoutes(routes);
    }

    private void loadRoutes(List<RouteInfo> list)
    {
        logger.log(Level.INFO, "fetchRoutes count = " + list.size());

        oracle.clear();
        for (RouteInfo routeInfo : list)
        {
            String key = routeInfo.getKey("");
            mapRouteInfo.put(key, routeInfo);
            oracle.add(key);
        }
    }

    private void fillSharingPanel()
    {
        sharingPanel.clear();
        if (Wizard.shareAvailable())
        {
            ClickHandler shareBook = new ClickHandler()
            {

                @Override
                public void onClick(ClickEvent event)
                {
                    wizard.onNextClick(null);

                }
            };
            buttonShare = ButtonFactory.getButton("Share.", "150px");
            buttonShare.addClickHandler(shareBook);
            sharingPanel.add(buttonShare);
        }
        buttonAnnounce = ButtonFactory.getButton("Announce share.", "150px");
        sharingPanel.add(buttonAnnounce);
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

    private Widget getDisclosure(String description)
    {
        String labelDescription0 = "no description!";
        String labelDescription1 = "no description!";
        if (description != null)
        {
            int defautlt = description.length() > 70 ? 70 : description.length();
            int breakCount = description.indexOf('.') + 1;
            breakCount = breakCount > 0 ? breakCount : defautlt;
            labelDescription0 = description.substring(breakCount);
            labelDescription1 = description.substring(0, breakCount);
        }
        Label descriptionLabel = new Label(labelDescription0);

        DisclosurePanel advancedDisclosure = new DisclosurePanel(labelDescription1);
        advancedDisclosure.setAnimationEnabled(true);
        advancedDisclosure.setContent(descriptionLabel);

        return advancedDisclosure;

    }

    public void displayRoute()
    {
        continueLoading();

    }

    private void continueLoading()
    {
        logger.log(Level.INFO, "continueLoading:");

        BOOKINGINFO.setPaidPrice(CurrencyHelper.getPriceInDollars(Wizard.ROUTEINFO, BOOKINGINFO.getCurrency(), BOOKINGINFO.getRate()));

        panelMotivation.setVisible(false);
        RouteInfo routeInfo = Wizard.ROUTEINFO;
        labelRouteName.setText(routeInfo.getKey(CurrencyHelper.getPrice(routeInfo, Wizard.BOOKINGINFO.getCurrency(), Wizard.BOOKINGINFO.getRate())));
        imageVehicle.setUrl("/imageservice?image=" + routeInfo.getImage());
        panelDescription.clear();
        panelDescription.add(getDisclosure(routeInfo.getDescription()));

        panelRoute.setVisible(true);
        TaxisurfrEntryPoint.SERVICE.getBookingsForRoute(Wizard.ROUTEINFO, new AsyncCallback<List<BookingInfo>>()
        {
            @Override
            public void onSuccess(List<BookingInfo> list)
            {
                Wizard.EXISTING_BOOKINGS_ON_ROUTE = list;
                fillSharingPanel();
            }

            @Override
            public void onFailure(Throwable caught)
            {
            }
        });

        TaxisurfrEntryPoint.SERVICE.getRatings(Wizard.ROUTEINFO, new AsyncCallback<List<RatingInfo>>()
        {
            @Override
            public void onSuccess(List<RatingInfo> ratings)
            {
                fp.clear();
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
        TaxisurfrEntryPoint.sendStat(routeInfo.getKey(""), StatInfo.Update.ROUTE);
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
