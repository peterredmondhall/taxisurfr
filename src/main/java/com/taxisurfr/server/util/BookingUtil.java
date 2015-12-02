package com.taxisurfr.server.util;

import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.server.entity.Profil;
import com.taxisurfr.server.entity.Route;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.RouteInfo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookingUtil
{
    private static final String ROUTE = "Route:";
    private static final String DATE = "Date:";
    private static final String FLIGHTNO = "Flight No:";
    private static final String HOTEL = "Hotel:";
    private static final String LANDING_TIME = "Landing Time:";
    private static final String PICKUP_TIME = "Pickup Time:";
    private static final String NAME = "Name:";
    private static final String EMAIL = "Email:";
    private static final String NUM_PAX = "Passengers:";
    private static final String NUM_SURFBOARDS = "Surfboards:";
    private static final String REQS = "Other requirements:";
    private static DateTimeFormatter sdf = DateTimeFormat.forPattern("dd.MM.yyyy");

    public static String toEmailText(BookingInfo bookingInfo)
    {
        List<Pair<String, String>> list = toPairList(bookingInfo);
        return toEmailTextFromList(list);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Deprecated
    public static List<Pair<String, String>> toPairList(BookingInfo bookingInfo)
    {
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

        list.add(Pair.of(ROUTE, bookingInfo.getRouteInfo().getKey("")));
        list.add(Pair.of(DATE, sdf.print(new DateTime(bookingInfo.getDate()))));
        RouteInfo.PickupType pickupType = bookingInfo.getRouteInfo().getPickupType();
        list.add(Pair.of(pickupType.getLocationType(), bookingInfo.getFlightNo()));
        list.add(Pair.of(pickupType.getTimeType(), bookingInfo.getLandingTime()));
        list.add(Pair.of(NAME, bookingInfo.getName()));
        list.add(Pair.of(EMAIL, bookingInfo.getEmail()));
        list.add(Pair.of(NUM_SURFBOARDS, Integer.toString(bookingInfo.getSurfboards())));
        list.add(Pair.of(NUM_PAX, Integer.toString(bookingInfo.getPax())));
        list.add(Pair.of(REQS, bookingInfo.getRequirements()));

        return list;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Pair<String, String>> toPairList(Booking booking, Route route)
    {
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

        list.add(Pair.of(ROUTE, route.getStart()+" to "+route.getEnd()));
        list.add(Pair.of(DATE, sdf.print(new DateTime(booking.getDate()))));
        RouteInfo.PickupType pickupType = route.getPickupType();
        list.add(Pair.of(pickupType.getLocationType(), booking.getFlightNo()));
        list.add(Pair.of(pickupType.getTimeType(), booking.getLandingTime()));
        list.add(Pair.of(NAME, booking.getName()));
        list.add(Pair.of(EMAIL, booking.getEmail()));
        list.add(Pair.of(NUM_SURFBOARDS, Integer.toString(booking.getSurfboards())));
        list.add(Pair.of(NUM_PAX, Integer.toString(booking.getPax())));
        list.add(Pair.of(REQS, booking.getRequirements()));

        return list;
    }

    public static String toEmailTextFromList(List<Pair<String, String>> list)
    {
        StringBuffer sb = new StringBuffer();
        for (Pair<String, String> pair : list)
        {
            sb.append(pair.first + "\t" + pair.second + "\r");
        }

        String msg = sb.toString();
        return msg;
    }

    public static String toConfirmationRequestHtml(BookingInfo bookingInfo, File file, Profil profil)
    {
        String html = getTemplate(file);
        html = toConfirmationEmailHtml(bookingInfo, html, profil);
        html = html.replace("AGREE_SHARE_LINK", profil.getTaxisurfUrl() + "?share=" + bookingInfo.getId());
        return html;
    }

    @Deprecated
    public static String toConfirmationEmailHtml(BookingInfo bookingInfo, File file, Profil profil)
    {
        String html = getTemplate(file);
        return toConfirmationEmailHtml(bookingInfo, html, profil);
    }

    public static String toConfirmationEmailHtml(Booking booking, Route route, File file, Profil profil)
    {
        String html = getTemplate(file);
        return toConfirmationEmailHtml(booking,route, html, profil);
    }

    private static String FACEBOOK_APP = "https://apps.facebook.com/1651399821757463";
    private static String FACEBOOK_PAGE = "https://www.facebook.com/taxisurfr";

    @Deprecated
    public static String toConfirmationEmailHtml(BookingInfo bookingInfo, String html, Profil profil)
    {
        String insertion = "";
        for (Pair<String, String> pair : toPairList(bookingInfo))
        {
            insertion += pair.first + " " + pair.second + "<br>";
        }
        html = html.replace("____INSERT___DETAILS___", insertion);

        String taxisurfrRouteLink = profil.getTaxisurfUrl() + "?route=" + bookingInfo.getRouteId();
        taxisurfrRouteLink = FACEBOOK_PAGE;

        html = html.replace("__TAXISURFR_ROUTE_LINK__", taxisurfrRouteLink);
        if (bookingInfo.getShareWanted())
        {
            html = html.replace("___SHARE_MESSAGE__", "Spread the word about your shared taxi using this share link.");

        }
        else
        {
            html = html.replace("___SHARE_MESSAGE__", "");
        }

        return html;

    }

    public static String toConfirmationEmailHtml(Booking booking, Route route, String html, Profil profil)
    {
        String insertion = "";
        for (Pair<String, String> pair : toPairList(booking,route))
        {
            insertion += pair.first + " " + pair.second + "<br>";
        }
        html = html.replace("____INSERT___DETAILS___", insertion);

        String taxisurfrRouteLink = profil.getTaxisurfUrl() + "?route=" + booking.getRoute();
        taxisurfrRouteLink = FACEBOOK_PAGE;

        html = html.replace("__TAXISURFR_ROUTE_LINK__", taxisurfrRouteLink);
        if (booking.getShareWanted())
        {
            html = html.replace("___SHARE_MESSAGE__", "Spread the word about your shared taxi using this share link.");

        }
        else
        {
            html = html.replace("___SHARE_MESSAGE__", "");
        }

        return html;

    }

    public static String getTemplate(File file)
    {

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return stringBuilder.toString();

    }

}
