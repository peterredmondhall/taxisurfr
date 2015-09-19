package com.taxisurfr.client.dashboard.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.DashboardEntryPoint;

public class DashboardVeiw extends Composite
{

    private static DashboardVeiwUiBinder uiBinder = GWT.create(DashboardVeiwUiBinder.class);

    interface DashboardVeiwUiBinder extends UiBinder<Widget, DashboardVeiw>
    {
    }

    @UiField
    HTMLPanel menuContainer;

    @UiField
    HTMLPanel dataContainer;

    @UiField
    Anchor bookingManagement;

    @UiField
    Anchor routeManagement;

    @UiField
    Anchor contractorManagement;

    @UiField
    Anchor financeManagement;

    @UiField
    Anchor adminManagement;

    FinanceManagementVeiw financeManagementView;

    private final HTMLPanel displayContainer = new HTMLPanel("");

    private static Boolean isAdmin = false;
    public DashboardVeiw()
    {
        initWidget(uiBinder.createAndBindUi(this));
        setMenu();
        dataContainer.add(displayContainer);
        isAdmin = Boolean.TRUE.equals(DashboardEntryPoint.getAgentInfo().isAdmin()) || DashboardEntryPoint.getAgentInfo().getEmail().equals("test@example.com");
        adminManagement.setVisible(isAdmin);

    }

    public static boolean isAdmin()
    {
        return isAdmin;
    }

    private void setMenu()
    {
        adminManagement.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                displayContainer.clear();
                displayContainer.add(new AdminManagementVeiw());
            }
        });

        bookingManagement.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                displayContainer.clear();
                displayContainer.add(new BookingManagementVeiw());
            }
        });

        routeManagement.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                displayContainer.clear();
                displayContainer.add(new RouteManagementVeiw());
            }
        });
        contractorManagement.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                displayContainer.clear();
                displayContainer.add(new ContractorManagementVeiw());
            }
        });
        financeManagement.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                displayContainer.clear();
                financeManagementView = new FinanceManagementVeiw();
                displayContainer.add(financeManagementView);
            }
        });
        displayContainer.add(new BookingManagementVeiw());
    }
}
