package com.taxisurfr.client.dashboard.ui;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.taxisurfr.client.DashboardEntryPoint;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.shared.model.FinanceInfo;

public class FinanceManagementVeiw extends Composite
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);
    private static DateTimeFormat sdf = DateTimeFormat.getFormat("dd.MM.yyyy");

    private static FinanceManagementVeiwUiBinder uiBinder = GWT.create(FinanceManagementVeiwUiBinder.class);

    interface FinanceManagementVeiwUiBinder extends UiBinder<Widget, FinanceManagementVeiw>
    {
    }

    private final CellTable.Resources tableRes = GWT.create(TableRes.class);

    public class FinanceInfoComparator implements Comparator<FinanceInfo>
    {

        @Override
        public int compare(FinanceInfo bi1, FinanceInfo bi2)
        {
            return (bi1.getDate().after(bi2.getDate())) ? -1 : 1;
        }
    }

    CellTable<FinanceInfo> summaryTable;
    NumberFormat usdFormat = NumberFormat.getFormat(".00");

    @UiField
    HTMLPanel mainPanel;
    @UiField
    HTMLPanel btnContainer;

    private Button addTransferBtn;

    private final SelectionModel<FinanceInfo> selectionModel = new MultiSelectionModel<FinanceInfo>(null);

    // The list of data to display.

    public FinanceManagementVeiw()
    {
        initWidget(uiBinder.createAndBindUi(this));
        fetchFinancess();
        setAddRouteBtn();
    }

    private void fetchFinancess()
    {
        service.getFinances(DashboardEntryPoint.getAgentInfo(), new AsyncCallback<List<FinanceInfo>>()
        {

            @Override
            public void onSuccess(List<FinanceInfo> result)
            {
                initWidget(result);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });
    }

    private void initWidget(List<FinanceInfo> financeList)
    {
        if (financeList != null && financeList.size() > 0)
        {
            mainPanel.clear();
            setSummaryCellTable(financeList);
            setTransferCellTable(financeList);
            setPaymentsCellTable(financeList);
        }
        addTransferBtn.setVisible(DashboardEntryPoint.isAdmin());
    }

    private CellTable<FinanceInfo> setPaymentsCellTable(List<FinanceInfo> financeList)
    {
        int tableSize = getTableSize(financeList, FinanceInfo.Type.PAYMENT);

        CellTable<FinanceInfo> table = new CellTable<FinanceInfo>(tableSize, tableRes);

        table.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<FinanceInfo>createCheckboxManager());

        // Create date column.
        TextColumn<FinanceInfo> dateColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo booking)
            {
                return sdf.format(booking.getDate());
            }
        };

        // Create requirements column.
        TextColumn<FinanceInfo> typeColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo booking)
            {
                return booking.getType().name();
            }
        };

        // Create requirements column.
        TextColumn<FinanceInfo> priceColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo booking)
            {
                if (booking.getAmount() != null)
                {
                    Double d = (double) booking.getAmount() / 100;
                    return usdFormat.format(d);
                }
                return "error";
            }
        };

        TextColumn<FinanceInfo> orderColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo info)
            {
                return info.getOrder();
            }
        };

        TextColumn<FinanceInfo> nameColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo info)
            {
                return info.getName();
            }
        };

        table.setTableLayoutFixed(true);
        // Add the columns.
        table.addColumn(typeColumn, "Type");
        table.addColumn(orderColumn, "Order");
        table.addColumn(nameColumn, "Name");
        table.addColumn(dateColumn, "Date");
        table.addColumn(priceColumn, "Amount USD");

        // Create a data provider.
        ListDataProvider<FinanceInfo> dataProvider = new ListDataProvider<FinanceInfo>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(table);

        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        setData(financeList, dataProvider, FinanceInfo.Type.PAYMENT);

        addTable(table, "400px");
        return table;
    }

    private int getTableSize(List<FinanceInfo> financeList, final FinanceInfo.Type type)
    {
        Predicate<FinanceInfo> predicate = new Predicate<FinanceInfo>()
        {
            @Override
            public boolean apply(FinanceInfo input)
            {
                return type.equals(input.getType());
            }
        };

        return FluentIterable
                .from(financeList)
                .filter(predicate).size();
    }

    private CellTable<FinanceInfo> setSummaryCellTable(List<FinanceInfo> financeList)
    {
        CellTable<FinanceInfo> summaryTable = new CellTable<FinanceInfo>(4, tableRes);

        summaryTable.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<FinanceInfo>createCheckboxManager());

        // Create requirements column.
        TextColumn<FinanceInfo> priceColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo booking)
            {
                if (booking.getAmount() != null)
                {
                    Double d = (double) booking.getAmount() / 100;
                    return usdFormat.format(d);
                }
                return "error";
            }
        };

        TextColumn<FinanceInfo> descColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo info)
            {
                return info.getName();
            }
        };

        summaryTable.setTableLayoutFixed(true);
        // Add the columns.
        summaryTable.addColumn(descColumn, "");
        summaryTable.addColumn(priceColumn, "Balance USD");

        // Create a data provider.
        ListDataProvider<FinanceInfo> dataProvider = new ListDataProvider<FinanceInfo>();

        // Connect the table to the data provider.

        dataProvider.addDataDisplay(summaryTable);

        FinanceInfo payments = new FinanceInfo();
        payments.setName("Total payments");
        Long paymentsAmt = 0L;
        Long currentPaymentsAmt = 0L;
        FinanceInfo transfers = new FinanceInfo();
        transfers.setName("Total transfers");
        Long transfersAmt = 0L;
        List<FinanceInfo> summary = Lists.newArrayList();
        FinanceInfo balance = new FinanceInfo();
        balance.setName("Balance");
        FinanceInfo currentBalance = new FinanceInfo();
        currentBalance.setName("Current Balance");
        summary.add(payments);
        summary.add(transfers);
        summary.add(balance);
        summary.add(currentBalance);

        for (FinanceInfo financeInfo : financeList)
        {
            switch (financeInfo.getType())
            {
                case PAYMENT:
                    paymentsAmt += financeInfo.getAmount();
                    if (financeInfo.getDeliveryDate() == null || financeInfo.getDeliveryDate().before(new Date()))
                    {
                        currentPaymentsAmt += financeInfo.getAmount();
                    }
                    break;
                case TRANSFER:
                    transfersAmt += financeInfo.getAmount();
                    break;
                default:
                    break;

            }
        }
        payments.setAmount(paymentsAmt);
        transfers.setAmount(transfersAmt);
        balance.setAmount(paymentsAmt - transfersAmt);
        currentBalance.setAmount(currentPaymentsAmt - transfersAmt);
        dataProvider.setList(summary);

        addTable(summaryTable, "120px");

        return summaryTable;
    }

    private void setData(List<FinanceInfo> financeList, ListDataProvider<FinanceInfo> dataProvider, final FinanceInfo.Type type)
    {
        Predicate<FinanceInfo> predicate = new Predicate<FinanceInfo>()
        {
            @Override
            public boolean apply(FinanceInfo input)
            {
                return type.equals(input.getType());
            }
        };
        dataProvider.setList(
                FluentIterable
                        .from(financeList)
                        .filter(predicate)
                        .toSortedList(new FinanceInfoComparator()));
    }

    private void setTransferCellTable(List<FinanceInfo> financeList)
    {
        int tableSize = getTableSize(financeList, FinanceInfo.Type.TRANSFER);
        CellTable<FinanceInfo> table = new CellTable<FinanceInfo>(tableSize, tableRes);

        table.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<FinanceInfo>createCheckboxManager());

        // Create date column.
        TextColumn<FinanceInfo> dateColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo booking)
            {
                return sdf.format(booking.getDate());
            }
        };

        TextColumn<FinanceInfo> priceColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo booking)
            {
                if (booking.getAmount() != null)
                {
                    Double d = (double) booking.getAmount() / 100;
                    return usdFormat.format(d);
                }
                return "error";
            }
        };

        TextColumn<FinanceInfo> nameColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo info)
            {
                return info.getName();
            }
        };
        // Create requirements column.
        TextColumn<FinanceInfo> typeColumn = new TextColumn<FinanceInfo>()
        {
            @Override
            public String getValue(FinanceInfo booking)
            {
                return booking.getType().name();
            }
        };

        table.setTableLayoutFixed(true);
        // Add the columns.
        table.addColumn(typeColumn, "Type");
        table.addColumn(nameColumn, "Reference");
        table.addColumn(dateColumn, "Date");
        table.addColumn(priceColumn, "Amount USD");

        // Create a data provider.
        ListDataProvider<FinanceInfo> dataProvider = new ListDataProvider<FinanceInfo>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(table);

        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        setData(financeList, dataProvider, FinanceInfo.Type.TRANSFER);

        addTable(table, "200px");
    }

    private void addTable(CellTable<FinanceInfo> table, String height)
    {
        table.getElement().getStyle().setMarginTop(2, Unit.PX);
        table.setWidth("100%");
        VerticalPanel panel = new VerticalPanel();
        panel.getElement().getStyle().setWidth(100, Unit.PCT);
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        panel.add(table);
        ScrollPanel scrollPanel = new ScrollPanel(panel);
        scrollPanel.setHeight(height);
        mainPanel.add(scrollPanel);
    }

    private void setAddRouteBtn()
    {
        addTransferBtn = new Button();
        addTransferBtn.setStyleName("btn btn-primary");
        addTransferBtn.setText("+Transfer");
        addTransferBtn.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                showEditPopup(event);

            }
        });
        addTransferBtn.getElement().getStyle().setFloat(Float.RIGHT);
        addTransferBtn.getElement().getStyle().setMargin(3, Unit.PX);
        btnContainer.add(addTransferBtn);
    }

    private void showEditPopup(ClickEvent event)
    {
        final TextBox editReferenceTxtBox = new TextBox();
        final TextBox editAmountUSDBox = new TextBox();

        final PopupPanel editPlacePopUpPanel = new PopupPanel(true);
        final VerticalPanel vPanel = new VerticalPanel();
        Grid grid = new Grid(9, 2);
        vPanel.add(grid);
        final Label errLbl = new Label();
        errLbl.setStyleName("errLbl");

        Button saveBtn = new Button("Save");
        saveBtn.setStyleName("btn btn-primary");
        saveBtn.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                FinanceInfo financeInfo = new FinanceInfo();
                financeInfo.setAmount(100L * Long.parseLong(editAmountUSDBox.getText()));
                financeInfo.setName(editReferenceTxtBox.getText());
                financeInfo.setDate(new Date());
                financeInfo.setDate(new Date());
                financeInfo.setAgentId(DashboardEntryPoint.getAgentInfo().getId());

                service.savePayment(financeInfo, new AsyncCallback<List<FinanceInfo>>()
                {

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        Window.alert("Failed to save transfer!");
                    }

                    @Override
                    public void onSuccess(List<FinanceInfo> finances)
                    {
                        editPlacePopUpPanel.setVisible(false);
                        initWidget(finances);
                    }
                });
            }
        });
        // Setting up Popup Panel
        int row = 0;

        final Label referenceLabel = new Label("Reference");
        final Label amountLabel = new Label("Amount USD");

        addPopupPanel(referenceLabel, editReferenceTxtBox, grid, row++);
        addPopupPanel(amountLabel, editAmountUSDBox, grid, row++);
        vPanel.add(saveBtn);
        vPanel.add(errLbl);
        editPlacePopUpPanel.add(vPanel);
        editPlacePopUpPanel.addStyleName("addNewPlacePopup");
        editPlacePopUpPanel.setPopupPosition(event.getClientX(), event.getClientY());
        editPlacePopUpPanel.show();

    }

    private void addPopupPanel(Label label, Widget widget, Grid grid, int row)
    {
        grid.setWidget(row, 0, label);
        grid.setWidget(row, 1, widget);
    }

}
