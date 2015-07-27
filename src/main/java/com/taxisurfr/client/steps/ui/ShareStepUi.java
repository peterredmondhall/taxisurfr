package com.taxisurfr.client.steps.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.taxisurfr.client.GwtWizard;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.shared.OrderType;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.StatInfo;

public class ShareStepUi extends Composite
{
    public static final Logger logger = Logger.getLogger(ShareStepUi.class.getName());

    private static ShareStepUiUiBinder uiBinder = GWT.create(ShareStepUiUiBinder.class);
    private static NumberFormat usdFormat = NumberFormat.getFormat(".00");
    private static DateTimeFormat sdf = DateTimeFormat.getFormat("dd.MM.yyyy");

    interface ShareStepUiUiBinder extends UiBinder<Widget, ShareStepUi>
    {
    }

    @UiField
    HTMLPanel mainPanel;

    @UiField
    VerticalPanel sharePanel, noSharePanel;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    Button buttonNewBooking;

    private CellTable<BookingInfo> cellTable;

    private Map<Long, BookingInfo> shareMap;
    private final Wizard wizard;

    public ShareStepUi(Wizard wizard)
    {
        initWidget(uiBinder.createAndBindUi(this));
        sharePanel.setVisible(false);
        noSharePanel.setVisible(false);
        this.wizard = wizard;
    }

    private void fillTable(String flightNoHote, String landingTimePickup)
    {
        cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

        TextColumn<BookingInfo> dateColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo object)
            {
                return sdf.format(object.getDate());
            }
        };
        cellTable.addColumn(dateColumn, "Date");

        TextColumn<BookingInfo> landingTimeColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo object)
            {
                return object.getLandingTime();
            }
        };
        cellTable.addColumn(landingTimeColumn, "approx. pickup time");

        TextColumn<BookingInfo> shareColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo object)
            {
                return "Click to share";
            }
        };
        cellTable.addColumn(shareColumn, "");

        // Add a text column to show the address.

        // Add a selection model to handle user selection.
        final SingleSelectionModel<BookingInfo> selectionModel = new SingleSelectionModel<BookingInfo>();
        cellTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {
            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
                BookingInfo selected = selectionModel.getSelectedObject();
                if (selected != null)
                {
                    BookingInfo bookingToShare = shareMap.get(selected.getId());

                    Wizard.BOOKINGINFO.setDate(bookingToShare.getDate());
                    Wizard.BOOKINGINFO.setFlightNo(bookingToShare.getFlightNo());
                    Wizard.BOOKINGINFO.setLandingTime(bookingToShare.getLandingTime());
                    Wizard.BOOKINGINFO.setOrderType(OrderType.SHARE);
                    Wizard.BOOKINGINFO.setParentId(bookingToShare.getId());

                    scrollPanel.remove(cellTable);
                    GwtWizard.sendStat("step:Share(share request)", StatInfo.Update.TYPE);
                    wizard.onNextClick(null);

                }
            }
        });

        // Set the total row count. This isn't strictly necessary, but it affects
        // paging calculations, so its good habit to keep the row count up to date.
        cellTable.setRowCount(Wizard.EXISTING_BOOKINGS_ON_ROUTE.size(), true);

        // Push the data into the widget.
        cellTable.setRowData(0, Wizard.EXISTING_BOOKINGS_ON_ROUTE);

    }

    public void show(boolean visible, Button prev)
    {

        prev.setEnabled(true);
        prev.setVisible(true);
        Wizard.BOOKINGINFO.setOrderType(OrderType.BOOKING);

        showShareNoShare();
    }

    private void showShareNoShare()
    {
        boolean shareAvailable = Wizard.EXISTING_BOOKINGS_ON_ROUTE.size() > 0;

        sharePanel.setVisible(shareAvailable);
        noSharePanel.setVisible(!shareAvailable);

        if (shareAvailable && cellTable == null)
        {
            cellTable = new CellTable<BookingInfo>();
            shareMap = new HashMap<>();
            for (BookingInfo bookingInfo : Wizard.EXISTING_BOOKINGS_ON_ROUTE)
            {
                shareMap.put(bookingInfo.getId(), bookingInfo);
            }
            logger.info("+++++++++++++++++++++++++++showShareNoShare" + shareMap.values().size());
            fillTable(Wizard.ROUTEINFO.getPickupType().getLocationType(), Wizard.ROUTEINFO.getPickupType().getTimeType());
            scrollPanel.add(cellTable);

            buttonNewBooking.addClickHandler(new ClickHandler()
            {

                @Override
                public void onClick(ClickEvent event)
                {
                    GwtWizard.sendStat("step:Share(new Booking)", StatInfo.Update.TYPE);
                    wizard.onNextClick(null);

                }
            });

        }
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

    public void removeTable()
    {
        if (cellTable != null)
        {
            scrollPanel.remove(cellTable);
            cellTable = null;
        }
    }
}
