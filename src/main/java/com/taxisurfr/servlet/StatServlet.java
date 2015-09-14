package com.taxisurfr.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.repackaged.com.google.api.client.util.Strings;
import com.google.common.collect.ImmutableMap;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.CurrencyManager;
import com.taxisurfr.server.StatManager;
import com.taxisurfr.shared.Currency;
import com.taxisurfr.shared.model.StatInfo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;

public class StatServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static final Logger log = Logger.getLogger(StatServlet.class.getName());
    private static final Map<String, Currency> currencyMap = new ImmutableMap.Builder()
            .put("GB", Currency.GBP)
            .put("CZ", Currency.EUR)
            .put("AU", Currency.AUD)
            .put("DE", Currency.EUR)
            .put("IT", Currency.EUR)
            .put("ES", Currency.EUR)
            .put("FR", Currency.EUR)
            .put("AT", Currency.EUR)
            .put("DK", Currency.EUR)
            .build();

    StatManager statManager = new StatManager();
    CurrencyManager currencyManager = new CurrencyManager();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
        ObjectifyService.begin();
        String country = req.getHeader("X-AppEngine-Country");
        String cityLatLong = req.getHeader("X-AppEngine-CityLatLong");
        String region = req.getHeader("X-AppEngine-Region");
        String city = req.getHeader("X-AppEngine-City");
        String src = req.getParameter("src");
        if (country == null || country.trim().length() == 0)
        {
            country = "XXX";
        }
        log.info("country:" + country);
        log.info("cityLatLong:" + cityLatLong);
        log.info("region:" + region);
        log.info("city:" + city);
        final String ip = req.getRemoteAddr();
        StatInfo statInfo = new StatInfo();
        statInfo.setDetail("country");
        statInfo.setSrc(src);
        statInfo.setCountry(country + ":" + city);
        statInfo.setIp(ip);

        PrintWriter writer = resp.getWriter();
        String currency = req.getParameter("curr");
        if (Strings.isNullOrEmpty(currency) || currency.equals("null"))
        {
            currency = currencyMap.get(country) != null ? currencyMap.get(country).name() : "USD";
        }
        Currency curr = Currency.valueOf(currency);
        Float rate = currencyMap.get(country) != null ? currencyManager.getRate(curr): 1.0f;

        statInfo.setCurrency(curr);
        statInfo.setCurrencyRate(rate);
        log.info("currency:" + curr);
        statInfo = statManager.createSessionStat(statInfo);

        ObjectMapper mapper = new ObjectMapper();
        String serialStatInfo = mapper.writeValueAsString(statInfo);

        writer.print(serialStatInfo);
        writer.close();

    }

}
