package com.taxisurfr.servlet;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.withDefaults;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.common.collect.Maps;
import com.taxisurfr.server.BookingServiceImpl;
import com.taxisurfr.server.ConfigManager;
import com.taxisurfr.server.CurrencyManager;
import com.taxisurfr.server.StatManager;
import com.taxisurfr.shared.Currency;

public class RatingServlet extends HttpServlet
{
    public static final Logger log = Logger.getLogger(RatingServlet.class.getName());
    private static final long serialVersionUID = 1L;
    BookingServiceImpl bookingServiceManager = new BookingServiceImpl();
    StatManager statManager = new StatManager();
    CurrencyManager currencyManager = new CurrencyManager();
    ConfigManager configManager = new ConfigManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        log.info("rating");
        bookingServiceManager.sendRatingRequest();
        statManager.report();
        loadCurrencyConversions(resp);

    }

    private void loadCurrencyConversions(HttpServletResponse resp)
    {
        try
        {
            Map<Currency, Float> map = Maps.newHashMap();
            URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
            URL url = new URL(
                    "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            HTTPRequest request = new HTTPRequest(url, HTTPMethod.GET, withDefaults().setDeadline(30.0));
            HTTPResponse response = urlFetchService.fetch(request);

            resp.setContentType("application/xml");
            byte[] content = response.getContent();
            String currency = new String(content);
            Scanner scanner = new Scanner(currency);
            scanner.next();
            scanner.useDelimiter("<Cube currency=");
            while (scanner.hasNext())
            {
                String curr = scanner.next();
                if (curr.contains("rate="))
                {
                    String code = curr.substring(1, 4);
                    String curr2 = curr.substring(curr.indexOf("rate='") + 6, curr.lastIndexOf("'"));
                    try
                    {
                        Float rate = Float.parseFloat(curr2);
                        Currency c = Currency.valueOf(code);
                        map.put(c, rate);
                    }
                    catch (Exception ex)
                    {

                    }
                }
            }
            scanner.close();
            currencyManager.update(map);
            resp.getOutputStream().write(content);
        }
        catch (Exception ex)
        {

        }
    }
}
