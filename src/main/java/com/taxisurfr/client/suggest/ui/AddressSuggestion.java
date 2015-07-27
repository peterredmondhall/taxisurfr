package com.taxisurfr.client.suggest.ui;

import java.io.Serializable;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

class AddressSuggestion implements Suggestion, Serializable
{

    private static final long serialVersionUID = 1L;

    String address;

    public AddressSuggestion(String address)
    {
        this.address = address;
    }

    @Override
    public String getDisplayString()
    {
        return this.address;
    }

    @Override
    public String getReplacementString()
    {
        return this.address;
    }
}