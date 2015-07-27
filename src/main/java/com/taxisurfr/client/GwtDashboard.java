package com.taxisurfr.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.taxisurfr.client.dashboard.ui.DashboardVeiw;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.shared.model.AgentInfo;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtDashboard implements EntryPoint
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);

    private static AgentInfo USERINFO;

    public static void setAgentInfo(AgentInfo agentInfo)
    {
        USERINFO = agentInfo;
    }

    public static AgentInfo getAgentInfo()
    {
        return USERINFO;
    }

    static boolean isAdmin = false;

    public static boolean isAdmin()
    {
        return isAdmin;
    }

    public static void setAdmin(boolean a)
    {
        isAdmin = a;
    }

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad()
    {
        service.getUser(new AsyncCallback<AgentInfo>()
        {

            @Override
            public void onSuccess(AgentInfo result)
            {
                if (result == null)
                {
                    Window.Location.replace("/login.html");
                }
                else
                {
                    USERINFO = result;
                    RootPanel.get().add(new DashboardVeiw());
                }

            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });
    }

}
