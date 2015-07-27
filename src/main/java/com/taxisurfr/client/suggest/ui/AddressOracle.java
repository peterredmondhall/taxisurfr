package com.taxisurfr.client.suggest.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderAddressComponent;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;

public class AddressOracle extends SuggestOracle
{
    private static String CITY = "Augsburg ";

    @Override
    public void requestSuggestions(final Request request,
            final Callback callback)
    {
        // this is the string, the user has typed so far
        String addressQuery = request.getQuery();
        if (addressQuery != null && addressQuery.length() > 2)
        {
            GeocoderRequest geocoderRequest = GeocoderRequest.create();
            geocoderRequest.setAddress(CITY + addressQuery);
            Geocoder geoCoder = Geocoder.create();
            geoCoder.geocode(geocoderRequest, new Geocoder.Callback()
            {

                @Override
                public void handle(JsArray<GeocoderResult> a, GeocoderStatus b)
                {
                    if (b == GeocoderStatus.OK)
                    {
                        List<Suggestion> suggestions = new ArrayList<Suggestion>();
                        for (int i = 0; i < a.length() && i < 5; i++)
                        {
                            GeocoderResult result = a.get(i);
                            JsArray<GeocoderAddressComponent> addressComponent = result.getAddressComponents();
                            for (int xx = 0; xx < addressComponent.length(); xx++)
                            {
                                GeocoderAddressComponent comp = addressComponent.get(xx);
                            }
                            String formattedAddress = result.getFormattedAddress();

                            AddressSuggestion newSuggestion = new AddressSuggestion(
                                    formattedAddress);
                            suggestions.add(newSuggestion);
                        }
                        Response response = new Response();
                        response.setSuggestions(suggestions);
                        callback.onSuggestionsReady(request, response);
                    }
                }
            });
        }
    }
}
