package com.taxisurfr.client.resources;

import com.google.gwt.i18n.client.Messages;

public interface ClientMessages extends Messages
{

    public String date();

    public String time();

    public String places();

    public String firstPage();

    public String secondPage();

    public String thirdPage();

    public String fourthPage();

    public String fifthPage();

    public String lastPage();

    public String dateRequiredError();

    public String timeRequiredError();

    public String placeRequiredError();

    public String organizerNameErrorMsg();

    public String organizerEmailErrorMsg();

    public String companionNameErrorMsg();

    public String companionEmailErrorMsg();

    public String confirmationMsg();

    public String mayNotBeEmptyErrorMsg();

    public String mustBeValidEmailErrorMsg();

    public String mustBeEqualEmail();

}
