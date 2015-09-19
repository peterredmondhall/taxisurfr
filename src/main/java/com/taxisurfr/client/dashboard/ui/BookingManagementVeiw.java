package com.taxisurfr.client.dashboard.ui;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.taxisurfr.client.DashboardEntryPoint;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.shared.OrderStatus;
import com.taxisurfr.shared.model.BookingInfo;

import java.util.Comparator;
import java.util.List;

public class BookingManagementVeiw extends Composite
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);
    private static DateTimeFormat sdf = DateTimeFormat.getFormat("dd.MM.yyyy");
    private Button cancelBtn;

    private static BookingManagementVeiwUiBinder uiBinder = GWT.create(BookingManagementVeiwUiBinder.class);

    interface BookingManagementVeiwUiBinder extends UiBinder<Widget, BookingManagementVeiw>
    {
    }

    private final CellTable.Resources tableRes = GWT.create(TableRes.class);
    private List<BookingInfo> BOOKINGS;

    public class BookingInfoComparator implements Comparator<BookingInfo>
    {

        @Override
        public int compare(BookingInfo bi1, BookingInfo bi2)
        {
            return (bi1.getDate().after(bi2.getDate())) ? 1 : -1;
        }
    }

    CellTable<BookingInfo> bookingManagementTable;

    @UiField
    HTMLPanel mainPanel;
    @UiField
    HTMLPanel btnContainer;

    private final SelectionModel<BookingInfo> selectionModel = new MultiSelectionModel<BookingInfo>(null);

    // The list of data to display.

    public BookingManagementVeiw()
    {
        initWidget(uiBinder.createAndBindUi(this));
        fetchBookings();
        setCancelBtn();
    }

    private void fetchBookings()
    {

        service.getBookings(DashboardEntryPoint.getAgentInfo(), new AsyncCallback<List<BookingInfo>>()
        {

            @Override
            public void onSuccess(List<BookingInfo> result)
            {
                fillTable(result);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });
    }

    private void fillTable(List<BookingInfo> result)
    {
        BOOKINGS = FluentIterable.from(result).filter(new Predicate<BookingInfo>()
        {

            @Override
            public boolean apply(BookingInfo input)
            {
                return OrderStatus.PAID.equals(input.getStatus());
            }
        }).toSortedList(new BookingInfoComparator());

        // if (BOOKINGS.size() > 0)
        {
            addTable();
            setCellTable();
        }
    }

    private void setCellTable()
    {
        bookingManagementTable.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<BookingInfo>createCheckboxManager());
        // Checkbox
        Column<BookingInfo, Boolean> checkColumn = new Column<BookingInfo, Boolean>(
                new CheckboxCell(true, false))
        {
            @Override
            public Boolean getValue(BookingInfo object)
            {
                return selectionModel.isSelected(object);
            }
        };

        // Create date column.
        TextColumn<BookingInfo> dateColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo booking)
            {
                return sdf.format(booking.getDate());
            }
        };

        // Create pax column.
        TextColumn<BookingInfo> paxColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo booking)
            {
                return String.valueOf(booking.getPax());
            }
        };
        // Create requirements column.
        TextColumn<BookingInfo> requirementsColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo booking)
            {
                return booking.getRequirements();
            }
        };

        TextColumn<BookingInfo> nameColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo booking)
            {
                return booking.getName();
            }
        };
        TextColumn<BookingInfo> emailColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo booking)
            {
                return booking.getEmail();
            }
        };

        TextColumn<BookingInfo> routeColumn = new TextColumn<BookingInfo>()
        {
            @Override
            public String getValue(BookingInfo booking)
            {
                return booking.getRouteInfo().getKey("");
            }
        };

//        private OrderStatus status;
//        private String flightNo;
//        private String landingTime;

        bookingManagementTable.setTableLayoutFixed(true);
        // Add the columns.
        bookingManagementTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        bookingManagementTable.addColumn(dateColumn, "Date");

        bookingManagementTable.addColumn(nameColumn, "Name");
        bookingManagementTable.addColumn(emailColumn, "Email");
        bookingManagementTable.addColumn(paxColumn, "No. passengers");
        bookingManagementTable.addColumn(requirementsColumn, "Requirements");
        bookingManagementTable.addColumn(routeColumn, "Route");

        bookingManagementTable.setColumnWidth(checkColumn, 40, Unit.PX);
        bookingManagementTable.setColumnWidth(dateColumn, 65, Unit.PX);
        bookingManagementTable.setColumnWidth(requirementsColumn, 200, Unit.PX);
        bookingManagementTable.setColumnWidth(paxColumn, 90, Unit.PX);
        bookingManagementTable.setColumnWidth(nameColumn, 160, Unit.PX);

        // Create a data provider.
        ListDataProvider<BookingInfo> dataProvider = new ListDataProvider<BookingInfo>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(bookingManagementTable);

        dataProvider.setList(BOOKINGS);

        // We know that the data is sorted alphabetically by default.
        // bookingManagementTable.getColumnSortList().push(forwardPickupPlaceColumn);
        bookingManagementTable.getElement().getStyle().setMarginTop(2, Unit.PX);
        bookingManagementTable.setWidth("100%");
    }

    private void addTable()
    {
        mainPanel.clear();
        bookingManagementTable = new CellTable<BookingInfo>(BOOKINGS.size(), tableRes);

        // Create a Pager to control the table.
        ScrollPanel panel = new ScrollPanel();
        panel.getElement().getStyle().setWidth(100, Unit.PCT);
        panel.setHeight("700px");
        panel.add(bookingManagementTable);
        mainPanel.add(panel);

    }

    private void setCancelBtn()
    {
        if (DashboardVeiw.isAdmin())
        {
            cancelBtn = new Button();
            cancelBtn.setStyleName("btn btn-primary");
            cancelBtn.setText("Delete");
            cancelBtn.addClickHandler(new ClickHandler()
            {

                @Override
                public void onClick(ClickEvent event)
                {
                    for (BookingInfo bookingInfo : BOOKINGS)
                    {
                        if (selectionModel.isSelected(bookingInfo))
                        {
                            service.cancelBooking(bookingInfo, DashboardEntryPoint.getAgentInfo(), new AsyncCallback<List<BookingInfo>>()
                            {

                                @Override
                                public void onFailure(Throwable caught)
                                {
                                    Refresh.refresh();
                                }

                                @Override
                                public void onSuccess(List<BookingInfo> result)
                                {
                                    fillTable(result);
                                    ;
                                }
                            });
                        }
                    }
                }
            });
            cancelBtn.getElement().getStyle().setFloat(Float.RIGHT);
            cancelBtn.getElement().getStyle().setMargin(3, Unit.PX);
            btnContainer.add(cancelBtn);
        }
    }
}
