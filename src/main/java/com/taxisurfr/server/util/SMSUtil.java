package com.taxisurfr.server.util;

import at.sms.business.sdk.client.impl.DefaultSmsClient;
import at.sms.business.sdk.domain.TextMessage;
import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.common.base.Joiner;
import com.google.common.primitives.Longs;
import com.taxisurfr.server.entity.Profil;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.RouteInfo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SMSUtil
{
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
    private static final Logger logger = Logger.getLogger(SMSUtil.class.getName());
    List<Long> recipients = Lists.newArrayList();

    public void add(Long recipient)
    {
        logger.info("adding " + recipient);
        if (recipient != null)
        {
            recipients.add(recipient);
        }
    }

    public void send(BookingInfo bookingInfo, Profil profil)
    {
        try
        {
            DefaultSmsClient smsClient = new DefaultSmsClient("dispatch@taxisurfr.com", profil.getSmspassword(),
                    "https://api.websms.com");
            String messageContent = Joiner.on("\r\n").join("**taxisurfr**",
                    bookingInfo.getRouteInfo().getKey(""),
                    sdf.format(bookingInfo.getDate()),
                    bookingInfo.getFlightNo(),
                    bookingInfo.getLandingTime()
            );
            long[] recips = Longs.toArray(Arrays.asList(FluentIterable.from(recipients).toArray(Long.class)));
            logger.info("send: to recepients" + Arrays.toString(recips));

            TextMessage textMessage = new
                    TextMessage(recips, messageContent);
            int maxSmsPerMessage = 1;
            boolean test = false;
            int statuscode = smsClient.send(textMessage,
                    maxSmsPerMessage, test);
            logger.info("status code:" + statuscode);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        SMSUtil smsUtil = new SMSUtil();
        smsUtil.add(491709025959L);
        BookingInfo bookingInfo = new BookingInfo();
        bookingInfo.setRouteInfo(new RouteInfo());
        bookingInfo.setDate(new Date());
        bookingInfo.setFlightNo("flightno");
        bookingInfo.setLandingTime("landingtime");
        smsUtil.send(bookingInfo, new Profil());
    }

}
