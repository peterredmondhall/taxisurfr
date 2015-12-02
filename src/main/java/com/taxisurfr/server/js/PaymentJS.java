package com.taxisurfr.server.js;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class PaymentJS {
    public String cardtoken;
    public Long bookingId;
}
