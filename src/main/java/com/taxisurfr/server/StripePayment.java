package com.taxisurfr.server;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.shared.model.BookingInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StripePayment
{
    private static final Logger logger = Logger.getLogger(StripePayment.class.getName());

    public String charge(String card, BookingInfo bookingInfo, String stripeSecret, String orderRef)
    {
        String error = null;
        try
        {
            Stripe.apiKey = stripeSecret;

            Map<String, Object> chargeParams = new HashMap<String, Object>();
            int cents = bookingInfo.getPaidPrice() * 100;
            chargeParams.put("amount", cents);
            chargeParams.put("currency", bookingInfo.getCurrency().name().toLowerCase());
            chargeParams.put("card", card); // obtained with Stripe.js
            chargeParams.put("description", "Taxi Charges Sri Lanka - "+orderRef+" - Thank you!");
            logger.info("receipt to "+bookingInfo.getEmail());
            chargeParams.put("receipt_email", bookingInfo.getEmail());
            //chargeParams.put("receipt_email", "hall@silvermobilityservices.com");


            logger.info("charging cents " + bookingInfo.getCurrency().name().toLowerCase() + cents);
            Charge charge = Charge.create(chargeParams);
            logger.info("charging successful");
            return error;
        }

        catch (Exception exception)
        {
            error = exception.getMessage();
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return error;
    }

    public String charge(String card, Booking booking, String stripeSecret)
    {
        String error = null;
        try
        {
            Stripe.apiKey = stripeSecret;

            Map<String, Object> chargeParams = new HashMap<String, Object>();
            int cents = booking.getPaidPrice() * 100;
            chargeParams.put("amount", cents);
            chargeParams.put("currency", booking.getCurrency().name().toLowerCase());
            chargeParams.put("card", card); // obtained with Stripe.js
            chargeParams.put("description", "Taxi Charges Sri Lanka - "+booking.getRef()+" - Thank you!");
            logger.info("receipt to "+booking.getEmail());
            chargeParams.put("receipt_email", booking.getEmail());
            //chargeParams.put("receipt_email", "hall@silvermobilityservices.com");


            logger.info("charging cents " + booking.getCurrency().name().toLowerCase() + cents);
            Charge charge = Charge.create(chargeParams);
            logger.info("charging successful");
            return error;
        }

        catch (Exception exception)
        {
            error = exception.getMessage();
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return error;
    }

}
