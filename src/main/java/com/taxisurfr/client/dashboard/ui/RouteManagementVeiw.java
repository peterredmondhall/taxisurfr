package com.taxisurfr.client.dashboard.ui;

import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.taxisurfr.client.DashboardEntryPoint;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.RouteInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RouteManagementVeiw extends Composite
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);

    private static PlacesManagementVeiwUiBinder uiBinder = GWT.create(PlacesManagementVeiwUiBinder.class);

    interface PlacesManagementVeiwUiBinder extends UiBinder<Widget, RouteManagementVeiw>
    {
    }

    private final CellTable.Resources tableRes = GWT.create(TableRes.class);
    private List<RouteInfo> ROUTES;
    private List<ContractorInfo> CONTRACTORS;
    NumberFormat usdFormat = NumberFormat.getFormat(".00");

    CellTable<RouteInfo> routeManagementTable;

    public class RouteInfoComparator implements Comparator<RouteInfo>
    {

        @Override
        public int compare(RouteInfo bi1, RouteInfo bi2)
        {
            return bi1.getStart().compareTo(bi2.getStart());
        }
    }

    @UiField
    HTMLPanel mainPanel;
    @UiField
    HTMLPanel btnContainer;
    @UiField
    VerticalPanel layout;
    private Button addPlaceBtn;
    private Button editPlaceBtn;
    private Button deletePlaceBtn;
    private final SelectionModel<RouteInfo> selectionModel = new MultiSelectionModel<RouteInfo>(null);
    private final RouteInfo.PickupType[] listPickupType;
    private Long imageId;

    final TextBox editStartTxtBox = new TextBox();
    final TextBox editEndTxtBox = new TextBox();
    final TextArea editDescriptionBox = new TextArea();
    final TextBox editPriceTxtBox = new TextBox();
    final TextBox editAgentCentsBox = new TextBox();

    final ListBox editPickupTypeBox = new ListBox();
    final ListBox editContractorTypeBox = new ListBox();
    final CheckBox editReturnCheckBox = new CheckBox();

    final Label startLabel = new Label("Start");
    final Label destinationLabel = new Label("Destination");
    final Label priceLabel = new Label("Price USD");
    final Label pickupLabel = new Label("Pickuptype");
    final Label contractorLabel = new Label("Contractor");
    final Label generateReturnLabel = new Label("Generate Return");

    final Label descLabel = new Label("Description");

    final Label imageLabel = new Label("Image");
    final Map<Long, ContractorInfo> contractorMap = Maps.newHashMap();
    private Long[] contractorIdList;

    // The list of data to display.

    public RouteManagementVeiw()
    {
        initWidget(uiBinder.createAndBindUi(this));
        listPickupType = new RouteInfo.PickupType[RouteInfo.PickupType.values().length];
        int i = 0;
        for (RouteInfo.PickupType t : RouteInfo.PickupType.values())
        {
            listPickupType[i++] = t;
        }
        initializeWidget();
    }

    private void initializeWidget()
    {
        ROUTES = new ArrayList<>();
        btnContainer.clear();
        mainPanel.clear();
        fetchContractorsAndRoutes();
    }

    private void initializeWidget(List<RouteInfo> routes)
    {
        ROUTES = routes;
        btnContainer.clear();
        mainPanel.clear();
        routeManagementTable = new CellTable<RouteInfo>(routes.size(), tableRes);
        setCellTable();
        setRouteManagementPanel();
    }

    private void fetchContractorsAndRoutes()
    {

        service.getContractors(DashboardEntryPoint.getAgentInfo(), new AsyncCallback<List<ContractorInfo>>()
        {

            @Override
            public void onSuccess(List<ContractorInfo> contractors)
            {
                CONTRACTORS = contractors;
                contractorIdList = new Long[CONTRACTORS.size()];
                int i = 0;
                for (ContractorInfo contractorInfo : CONTRACTORS)
                {
                    contractorMap.put(contractorInfo.getId(), contractorInfo);
                    contractorIdList[i++] = contractorInfo.getId();
                }
                fetchRoutes();

            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });
    }

    private void fetchRoutes()
    {

        service.getRoutesByAgent(DashboardEntryPoint.getAgentInfo(), new AsyncCallback<List<RouteInfo>>()
        {

            @Override
            public void onSuccess(List<RouteInfo> routes)
            {
                ROUTES = routes;

                routeManagementTable = new CellTable<RouteInfo>(routes.size(), tableRes);
                setCellTable();
                setRouteManagementPanel();
            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });
    }

    private void setRouteManagementPanel()
    {
        setDeleteRouteBtn();
        setUpdateRouteBtn();
        setAddRouteBtn();
    }

    private void setAddRouteBtn()
    {
        addPlaceBtn = new Button();
        addPlaceBtn.setStyleName("btn btn-primary");
        addPlaceBtn.setText("Add");
        addPlaceBtn.addClickHandler(new RouteAddEditClickHandler(RouteInfo.SaveMode.ADD));
        addPlaceBtn.getElement().getStyle().setFloat(Float.RIGHT);
        addPlaceBtn.getElement().getStyle().setMargin(3, Unit.PX);
        btnContainer.add(addPlaceBtn);
    }

    private void setDeleteRouteBtn()
    {
        deletePlaceBtn = new Button();
        deletePlaceBtn.setStyleName("btn btn-primary");
        deletePlaceBtn.setText("Delete");
        deletePlaceBtn.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                for (RouteInfo p : ROUTES)
                {
                    if (selectionModel.isSelected(p))
                    {
                        service.deleteRoute(DashboardEntryPoint.getAgentInfo(), p, new AsyncCallback<List<RouteInfo>>()
                        {

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                Refresh.refresh();
                            }

                            @Override
                            public void onSuccess(List<RouteInfo> result)
                            {
                                initializeWidget(result);
                            }
                        });
                    }
                }
            }
        });
        deletePlaceBtn.getElement().getStyle().setFloat(Float.RIGHT);
        deletePlaceBtn.getElement().getStyle().setMargin(3, Unit.PX);
        btnContainer.add(deletePlaceBtn);
    }

    private void setUpdateRouteBtn()
    {
        editPlaceBtn = new Button();
        editPlaceBtn.setStyleName("btn btn-primary");
        editPlaceBtn.setText("Edit");
        editPlaceBtn.addClickHandler(new RouteAddEditClickHandler(RouteInfo.SaveMode.UPDATE));

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
        routeManagementTable.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<RouteInfo>createCheckboxManager());

        // Checkbox
        Column<RouteInfo, Boolean> checkColumn = new Column<RouteInfo, Boolean>(
                new CheckboxCell(true, false))
        {
            @Override
            public Boolean getValue(RouteInfo object)
            {
                return selectionModel.isSelected(object);
            }
        };

        Column<RouteInfo, ImageResource> imageColumn =
                new Column<RouteInfo, ImageResource>(new ImageResourceCell())
                {
                    @Override
                    public ImageResource getValue(final RouteInfo routeInfo)
                    {
                        return new ArugamImageResource(routeInfo);
                    }
                };

        TextColumn<RouteInfo> startColumn = new TextColumn<RouteInfo>()
        {
            @Override
            public String getValue(RouteInfo route)
            {
                return route.getStart();
            }
        };

        TextColumn<RouteInfo> endColumn = new TextColumn<RouteInfo>()
        {
            @Override
            public String getValue(RouteInfo route)
            {
                return route.getEnd();
            }
        };

        TextColumn<RouteInfo> priceColumn = new TextColumn<RouteInfo>()
        {
            @Override
            public String getValue(RouteInfo route)
            {
                if (route.getCents() != null)
                {
                    Double d = (double) route.getCents() / 100;
                    return usdFormat.format(d);
                }
                return null;
            }
        };
        TextColumn<RouteInfo> agentCentsColumn = new TextColumn<RouteInfo>()
        {
            @Override
            public String getValue(RouteInfo route)
            {
                if (route.getAgentCents() != null)
                {
                    Double d = (double) route.getAgentCents() / 100;
                    return usdFormat.format(d);
                }
                return null;
            }
        };

        TextColumn<RouteInfo> descriptionColumn = new TextColumn<RouteInfo>()
        {
            @Override
            public String getValue(RouteInfo route)
            {
                if (route.getDescription() == null)
                {
                    return "";
                }
                if (route.getDescription().length() > 20)
                {
                    return route.getDescription().substring(0, 20) + "...";
                }
                return route.getDescription();
            }
        };

        TextColumn<RouteInfo> pickuptypeColumn = new TextColumn<RouteInfo>()
        {
            @Override
            public String getValue(RouteInfo route)
            {
                if (route.getPickupType() != null)
                {
                    return route.getPickupType().name();
                }
                return "error";
            }
        };

        TextColumn<RouteInfo> contractorColumn = new TextColumn<RouteInfo>()
        {
            @Override
            public String getValue(RouteInfo route)
            {
                Long contractorId = route.getContractorId();
                ContractorInfo contractorInfo = contractorMap.get(contractorId);
                return contractorInfo.getName();
            }
        };

        routeManagementTable.setTableLayoutFixed(true);
        // Add the columns.

        routeManagementTable.addColumn(checkColumn, "Select");
        routeManagementTable.addColumn(imageColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        routeManagementTable.addColumn(startColumn, "Start");
        routeManagementTable.addColumn(endColumn, "End");
        routeManagementTable.addColumn(priceColumn, "Price USD");
        routeManagementTable.addColumn(agentCentsColumn, "Agent Price USD");
        routeManagementTable.addColumn(descriptionColumn, "Description");
        routeManagementTable.addColumn(pickuptypeColumn, "Pickuptype");
        routeManagementTable.addColumn(contractorColumn, "Contractor");

        // Create a data provider.
        ListDataProvider<RouteInfo> dataProvider = new ListDataProvider<RouteInfo>();

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(routeManagementTable);

        dataProvider.setList(
                FluentIterable
                        .from(ROUTES)
                        .toSortedList(new RouteInfoComparator()));

        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(routeManagementTable);

        // We know that the data is sorted alphabetically by default.
        // bookingManagementTable.getColumnSortList().push(forwardPickupPlaceColumn);
        routeManagementTable.getElement().getStyle().setMarginTop(2, Unit.PX);
        routeManagementTable.setWidth("100%");
        VerticalPanel panel = new VerticalPanel();
        panel.getElement().getStyle().setWidth(100, Unit.PCT);
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        panel.add((routeManagementTable));
        // panel.add(pager);
        ScrollPanel scrollPanel = new ScrollPanel(panel);
        scrollPanel.setHeight("700px");
        mainPanel.add(scrollPanel);
    }

    private Widget getUploader(final Button saveButton)
    {
        final FormPanel form = new FormPanel();
        form.setAction("/.gupld");

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multipart MIME encoding.
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        // Create a panel to hold all of the form widgets.
        VerticalPanel panel = new VerticalPanel();
        form.setWidget(panel);

        // Create a FileUpload widget.
        FileUpload upload = new FileUpload();
        upload.setName("uploadFormElement");
        panel.add(upload);

        // Add a 'submit' button.
        panel.add(new Button("Upload file", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                saveButton.setEnabled(false);
                form.submit();
            }
        }));

        form.addSubmitCompleteHandler(new SubmitCompleteHandler()
        {

            @Override
            public void onSubmitComplete(SubmitCompleteEvent event)
            {
                String[] parts = event.getResults().split(("\\*\\*\\*"));
                imageId = Long.parseLong(parts[1]);
                saveButton.setEnabled(true);
            }
        });

        return form;

    }

    class RouteAddEditClickHandler implements ClickHandler
    {
        RouteInfo.SaveMode mode;

        public RouteAddEditClickHandler(RouteInfo.SaveMode mode)
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
            for (RouteInfo p : ROUTES)
            {
                if (selectionModel.isSelected(p))
                    count++;
            }
            if (count == 1)
            {
                for (RouteInfo p : ROUTES)
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
            showEditPopup(new RouteInfo(), 0, mode, event);
        }

    }

    private void showEditPopup(RouteInfo ri, final long routeId, final RouteInfo.SaveMode mode, ClickEvent event)
    {
        final PopupPanel editPlacePopUpPanel = new PopupPanel(true);
        final VerticalPanel vPanel = new VerticalPanel();
        Grid grid = new Grid(9, 2);
        vPanel.add(grid);
        final Label errLbl = new Label();
        errLbl.setStyleName("errLbl");

        editStartTxtBox.setText(ri.getStart());
        editEndTxtBox.setText(ri.getEnd());
        editDescriptionBox.setText(ri.getDescription());
        imageId = ri.getImage();

        if (ri.getCents() != null)
        {
            String price = usdFormat.format(ri.getCents() / 100);
            editPriceTxtBox.setText(price);
        }

        String agentCents = "";
        if (ri.getAgentCents() != null)
        {
            agentCents += ri.getAgentCents();
        }
        editAgentCentsBox.setText(agentCents);

        int i = 0;
        editPickupTypeBox.clear();
        for (RouteInfo.PickupType t : RouteInfo.PickupType.values())
        {
            editPickupTypeBox.addItem(t.name());
            if (t.equals(ri.getPickupType()))
            {
                editPickupTypeBox.setSelectedIndex(i);
            }
            i++;
        }

        i = 0;
        editContractorTypeBox.clear();
        for (ContractorInfo contractorInfo : CONTRACTORS)
        {
            editContractorTypeBox.addItem(contractorInfo.getName());
            if (contractorInfo.getId().equals(ri.getContractorId()))
            {
                editContractorTypeBox.setSelectedIndex(i);
            }
            i++;
        }

        Button saveBtn = new Button("Save");
        saveBtn.setStyleName("btn btn-primary");
        saveBtn.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                RouteInfo routeInfo = new RouteInfo();
                int selectedContractorIndex = editContractorTypeBox.getSelectedIndex();
                ContractorInfo contractorInfo = CONTRACTORS.get(selectedContractorIndex);
                routeInfo.setContractorId(contractorInfo.getId());
                try
                {
                    String price = editPriceTxtBox.getText();
                    Double priceL = Double.parseDouble(price);
                    Double cents = priceL * 100;
                    routeInfo.setCents(Math.round(cents));

                }
                catch (Exception ex)
                {
                    errLbl.setText("Enter a price");
                    return;
                }
                try
                {
                    String agentCents = editAgentCentsBox.getText();
                    Long agentCentsL = Long.parseLong(agentCents);
                    routeInfo.setAgentCents(agentCentsL);

                }
                catch (Exception ex)
                {
                }
                if (
                Strings.isNullOrEmpty(editStartTxtBox.getText()) ||
                        Strings.isNullOrEmpty(editEndTxtBox.getText()) ||
                        Strings.isNullOrEmpty(editPriceTxtBox.getText())
                )
                {
                    errLbl.setText("Please fill up all fields");
                    return;
                }
                else
                {
                    routeInfo.setId(routeId);
                    routeInfo.setStart(editStartTxtBox.getText());
                    routeInfo.setEnd(editEndTxtBox.getText());
                    routeInfo.setDescription(editDescriptionBox.getText());
                    routeInfo.setPickupType(listPickupType[editPickupTypeBox.getSelectedIndex()]);
                    routeInfo.setContractorId(contractorIdList[editContractorTypeBox.getSelectedIndex()]);

                    if (imageId != null)
                    {
                        routeInfo.setImage(imageId);
                    }
                    RouteInfo.SaveMode txSaveMode = RouteInfo.SaveMode.UPDATE;
                    if (mode.equals(RouteInfo.SaveMode.ADD))
                    {
                        txSaveMode = editReturnCheckBox.getValue() ? RouteInfo.SaveMode.ADD_WITH_RETURN : RouteInfo.SaveMode.ADD;
                    }

                    service.saveRoute(DashboardEntryPoint.getAgentInfo(), routeInfo, txSaveMode, new AsyncCallback<List<RouteInfo>>()
                    {

                        @Override
                        public void onFailure(Throwable caught)
                        {
                            Window.alert("Failed to save place!");
                        }

                        @Override
                        public void onSuccess(List<RouteInfo> routes)
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

        final Label startLabel = new Label("Start");
        final Label destinationLabel = new Label("Destination");
        final Label priceLabel = new Label("Price USD");
        final Label agentCentsLabel = new Label("Agent Cents");
        final Label pickupLabel = new Label("Pickuptype");
        final Label contractorLabel = new Label("Contractor");
        final Label generateReturnLabel = new Label("Generate Return");

        final Label descLabel = new Label("Description");

        final Label imageLabel = new Label("Image");

        addPopupPanel(startLabel, editStartTxtBox, grid, row++);
        addPopupPanel(destinationLabel, editEndTxtBox, grid, row++);
        addPopupPanel(priceLabel, editPriceTxtBox, grid, row++);
        if (Boolean.TRUE.equals(DashboardVeiw.isAdmin()))
        {
            addPopupPanel(agentCentsLabel, editAgentCentsBox, grid, row++);
        }
        addPopupPanel(pickupLabel, editPickupTypeBox, grid, row++);
        addPopupPanel(contractorLabel, editContractorTypeBox, grid, row++);
        if (mode.equals(RouteInfo.SaveMode.ADD))
        {
            addPopupPanel(generateReturnLabel, editReturnCheckBox, grid, row++);
        }
        addPopupPanel(descLabel, editDescriptionBox, grid, row++);
        addPopupPanel(imageLabel, getUploader(saveBtn), grid, row++);

        editPlaceBtn.getElement().getStyle().setFloat(Float.RIGHT);
        vPanel.add(saveBtn);
        vPanel.add(errLbl);
        editPlacePopUpPanel.add(vPanel);
        editPlacePopUpPanel.addStyleName("addNewPlacePopup");
        editPlacePopUpPanel.setPopupPosition(event.getClientX(), event.getClientY());
        editPlacePopUpPanel.show();

    }
}
