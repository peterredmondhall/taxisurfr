package com.taxisurfr.client.suggest.ui;

import com.google.gwt.user.client.ui.SuggestBox;

public class GoogleMapsSuggestBox extends SuggestBox
{
    public GoogleMapsSuggestBox()
    {
        super(new AddressOracle());
    }
}
