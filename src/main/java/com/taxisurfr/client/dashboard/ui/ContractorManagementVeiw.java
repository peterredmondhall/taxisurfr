package com.taxisurfr.client.dashboard.ui;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
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
import com.taxisurfr.client.GwtDashboard;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.shared.model.ContractorInfo;

public class ContractorManagementVeiw extends Composite
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);

    private static ContractorsManagementVeiwUiBinder uiBinder = GWT.create(ContractorsManagementVeiwUiBinder.class);

    interface ContractorsManagementVeiwUiBinder extends UiBinder<Widget, ContractorManagementVeiw>
    {
    }

    private final CellTable.Resources tableRes = GWT.create(TableRes.class);
    private List<ContractorInfo> CONTRACTORS;

    CellTable<ContractorInfo> contractorManagementTable;

    @UiField
    HTMLPanel mainPanel;
    @UiField
    HTMLPanel btnContainer;
    @UiField
    VerticalPanel layout;
    private Button addPlaceBtn;
    private Button editPlaceBtn;
    // private Button deletePlaceBtn;
    private final SelectionModel<ContractorInfo> selectionModel = new MultiSelectionModel<ContractorInfo>(null);

    final TextBox editContractorNameTxtBox = new TextBox();
    final TextBox editContractorEmailTxtBox = new TextBox();

    final TextBox[] addrNameTxtBox = { new TextBox(), new TextBox(), new TextBox(), new TextBox() };

    final Label nameLabel = new Label("Name");

    // The list of data to display.

    public ContractorManagementVeiw()
    {
        initWidget(uiBinder.createAndBindUi(this));
        initializeWidget();
    }

    private void initializeWidget()
    {
        CONTRACTORS = new ArrayList<>();
        btnContainer.clear();
        mainPanel.clear();
        contractorManagementTable = new CellTable<ContractorInfo>(13, tableRes);
        fetchContractors();
    }

//    private void initializeWidget(List<ContractorInfo> routeInfo)
//    {
//        CONTRACTORS = routeInfo;
//        btnContainer.clear();
//        mainPanel.clear();
//        contractorManagementTable = new CellTable<ContractorInfo>(13, tableRes);
//        setCellTable();
//        setContractorManagementPanel();
//    }

    private void fetchContractors()
    {

        service.getContractors(GwtDashboard.getAgentInfo(), new AsyncCallback<List<ContractorInfo>>()
        {

            @Override
            public void onSuccess(List<ContractorInfo> routes)
            {

                for (ContractorInfo route : routes)
                {
                    CONTRACTORS.add(route);
                }
                setCellTable();
                setContractorManagementPanel();
            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });
    }

    private void setContractorManagementPanel()
    {
        setUpdateContracttorBtn();
        setAddContractorBtn();
    }

    private void setAddContractorBtn()
    {
        addPlaceBtn = new Button();
        addPlaceBtn.setStyleName("btn btn-primary");
        addPlaceBtn.setText("Add");
        addPlaceBtn.addClickHandler(new AddEditClickHandler(ContractorInfo.SaveMode.ADD));
        addPlaceBtn.getElement().getStyle().setFloat(Float.RIGHT);
        addPlaceBtn.getElement().getStyle().setMargin(3, Unit.PX);
        btnContainer.add(addPlaceBtn);
    }

    private void setUpdateContracttorBtn()
    {
        editPlaceBtn = new Button();
        editPlaceBtn.setStyleName("btn btn-primary");
        editPlaceBtn.setText("Edit");
        editPlaceBtn.addClickHandler(new AddEditClickHandler(ContractorInfo.SaveMode.UPDATE));

        editPlaceBtn.getElement().getStyle().setFloat(Float.RIGHT);
        editPlaceBtn.getElement().getStyle().setMargin(3, Unit.PX);
        btnContainer.add(editPlaceBtn);
    }

    private void addPopupPanel(Label label, Widget widget, Grid grid, int row)
    {
        grid.setWidget(row, 0, label);
        grid.setWidget(row, 1, widget);
    }

    private void setCellTable()
    {
        contractorManagementTable.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<ContractorInfo>createCheckboxManager());

        // Checkbox
        Column<ContractorInfo, Boolean> checkColumn = new Column<ContractorInfo, Boolean>(
                new CheckboxCell(true, false))
        {
            @Override
            public Boolean getValue(ContractorInfo object)
            {
                return selectionModel.isSelected(object);
            }
        };

        TextColumn<ContractorInfo> nameColumn = new TextColumn<ContractorInfo>()
        {
            @Override
            public String getValue(ContractorInfo contractor)
            {
                return contractor.getName();
            }
        };

        TextColumn<ContractorInfo> emailColumn = new TextColumn<ContractorInfo>()
        {
            @Override
            public String getValue(ContractorInfo contractor)
            {
                return contractor.getEmail();
            }
        };

        TextColumn<ContractorInfo> addressColumn = new TextColumn<ContractorInfo>()
        {
            @Override
            public String getValue(ContractorInfo contractor)
            {
                return Joiner.on(",").skipNulls().join(contractor.getAddress());
            }
        };

        contractorManagementTable.setTableLayoutFixed(true);
        // Add the columns.

        contractorManagementTable.addColumn(checkColumn, "Select");
        contractorManagementTable.addColumn(nameColumn, "Contractor");
        contractorManagementTable.addColumn(emailColumn, "Email");
        contractorManagementTable.addColumn(addressColumn, "Address");

        // Create a data provider.
        ListDataProvider<ContractorInfo> dataProvider = new ListDataProvider<ContractorInfo>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(contractorManagementTable);

        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        List<ContractorInfo> list = dataProvider.getList();
        for (ContractorInfo booking : CONTRACTORS)
        {
            list.add(booking);
        }

        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(contractorManagementTable);

        // We know that the data is sorted alphabetically by default.
        // bookingManagementTable.getColumnSortList().push(forwardPickupPlaceColumn);
        contractorManagementTable.getElement().getStyle().setMarginTop(2, Unit.PX);
        contractorManagementTable.setWidth("100%");
        VerticalPanel panel = new VerticalPanel();
        panel.getElement().getStyle().setWidth(100, Unit.PCT);
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        panel.add((contractorManagementTable));
        // panel.add(pager);
        ScrollPanel scrollPanel = new ScrollPanel(panel);
        scrollPanel.setHeight("400px");
        mainPanel.add(scrollPanel);
    }

    class AddEditClickHandler implements ClickHandler
    {
        ContractorInfo.SaveMode mode;

        public AddEditClickHandler(ContractorInfo.SaveMode mode)
        {
            this.mode = mode;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            switch (mode)
            {
                case UPDATE:
                    update(event);
                    break;
                case ADD:
                    add(event);
                    break;
            }
        }

        public void update(ClickEvent event)
        {
            int count = 0;
            for (ContractorInfo p : CONTRACTORS)
            {
                if (selectionModel.isSelected(p))
                    count++;
            }
            if (count == 1)
            {
                for (ContractorInfo p : CONTRACTORS)
                {
                    if (selectionModel.isSelected(p))
                    {
                        final Long routeId = p.getId();
                        showEditPopup(p, routeId, mode, event);

                    }
                }
            }
            else
                Window.alert("Select one row to edit");
        }

        public void add(ClickEvent event)
        {
            showEditPopup(new ContractorInfo(), 0, mode, event);
        }

    }

    private void showEditPopup(ContractorInfo ri, final long routeId, final ContractorInfo.SaveMode mode, ClickEvent event)
    {
        final PopupPanel editPlacePopUpPanel = new PopupPanel(true);
        final VerticalPanel vPanel = new VerticalPanel();
        Grid grid = new Grid(8, 2);
        vPanel.add(grid);
        final Label errLbl = new Label();
        errLbl.setStyleName("errLbl");

        editContractorNameTxtBox.setText(ri.getName());
        editContractorEmailTxtBox.setText(ri.getEmail());
        for (int i = 0; i < addrNameTxtBox.length; i++)
        {
            addrNameTxtBox[i].setText(ri.getAddress() != null && ri.getAddress().size() > i ? ri.getAddress().get(i) : "");
        }
        Button saveBtn = new Button("Save");
        saveBtn.setStyleName("btn btn-primary");
        saveBtn.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                ContractorInfo contractorInfo = new ContractorInfo();
                if (
                isNullOrEmpty(editContractorNameTxtBox.getText()) ||
                        isNullOrEmpty(editContractorEmailTxtBox.getText())

                )
                {
                    errLbl.setText("Please fill up all fields");
                    return;
                }
                else
                {
                    contractorInfo.setId(routeId);
                    contractorInfo.setAgentId(GwtDashboard.getAgentInfo().getId());
                    contractorInfo.setName(editContractorNameTxtBox.getText());
                    contractorInfo.setEmail(editContractorEmailTxtBox.getText());
                    List<String> addr = Lists.newArrayList();
                    for (int i = 0; i < addrNameTxtBox.length; i++)
                    {
                        addr.add(addrNameTxtBox[i].getText());
                    }
                    contractorInfo.setAddress(addr);
                    service.saveContractor(GwtDashboard.getAgentInfo(), contractorInfo, mode, new AsyncCallback<List<ContractorInfo>>()
                    {

                        @Override
                        public void onFailure(Throwable caught)
                        {
                            Window.alert("Failed to save place!");
                        }

                        @Override
                        public void onSuccess(List<ContractorInfo> routes)
                        {
                            editPlacePopUpPanel.setVisible(false);
                            initializeWidget();
                        }
                    });
                }
            }
        });
        // Setting up Popup Panel
        int row = 0;

        final Label nameLabel = new Label("Contractor");
        final Label emailLabel = new Label("Email");

        addPopupPanel(nameLabel, editContractorNameTxtBox, grid, row++);
        addPopupPanel(emailLabel, editContractorEmailTxtBox, grid, row++);
        addPopupPanel(new Label("Address 1"), addrNameTxtBox[0], grid, row++);
        addPopupPanel(new Label("Address 2"), addrNameTxtBox[1], grid, row++);
        addPopupPanel(new Label("Address 3"), addrNameTxtBox[2], grid, row++);
        addPopupPanel(new Label("Address 4"), addrNameTxtBox[3], grid, row++);

        editPlaceBtn.getElement().getStyle().setFloat(Float.RIGHT);
        vPanel.add(saveBtn);
        vPanel.add(errLbl);
        editPlacePopUpPanel.add(vPanel);
        editPlacePopUpPanel.addStyleName("addNewPlacePopup");
        editPlacePopUpPanel.setPopupPosition(event.getClientX(), event.getClientY());
        editPlacePopUpPanel.show();

    }
}
