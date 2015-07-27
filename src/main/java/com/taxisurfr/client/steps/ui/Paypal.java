package com.taxisurfr.client.steps.ui;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.Refresh;
import com.taxisurfr.client.service.BookingService;
import com.taxisurfr.client.service.BookingServiceAsync;
import com.taxisurfr.shared.model.ProfilInfo;

public class Paypal extends Composite
{
    private final BookingServiceAsync service = GWT.create(BookingService.class);
    private static PaypalBinder uiBinder = GWT.create(PaypalBinder.class);

    interface PaypalBinder extends UiBinder<Widget, Paypal>
    {
    }

    @UiField
    HTMLPanel paypalPanel;

    public Paypal()
    {

        initWidget(uiBinder.createAndBindUi(this));

        String params = "";
        Map<String, List<String>> map = Window.Location.getParameterMap();
        for (String key : map.keySet())
        {
            if (params.length() == 0)
            {
                params = "?" + params;
            }
            else
            {
                params = "&" + params;
            }
            params += (key + "=\"" + map.get(key) + "\"");
        }
        String returnPath = Window.Location.getHost() + ":" + Window.Location.getPath() + params;
        paypalPanel.getElementById("hiddenReturn").setAttribute("value", returnPath);
        paypalPanel.getElementById("hiddenCancelReturn").setAttribute("value", returnPath);

    }

    public void setUrl()
    {
        service.getPaypalProfil(new AsyncCallback<ProfilInfo>()
        {

            @Override
            public void onSuccess(ProfilInfo profil)
            {
                paypalPanel.getElementById("paypalForm").setAttribute("action", profil.getPaypalUrl());
                paypalPanel.getElementById("hiddenBusiness").setAttribute("value", profil.getPaypalAccount());
                // paypalPanel.getElementById("hiddenAmount").setAttribute("value", profil.getPrice() != null ? profil.getPrice() : "160");

            }

            @Override
            public void onFailure(Throwable caught)
            {
                Refresh.refresh();
            }
        });

    }

    @Override
    public void setVisible(boolean visible)
    {
        paypalPanel.setVisible(visible);
    }

}
