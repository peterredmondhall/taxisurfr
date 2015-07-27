package com.taxisurfr.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.taxisurfr.server.entity.Profil;
import com.taxisurfr.shared.OrderStatus;

public class PaypalPaymentChecker
{

    public static final String TEST_PAYPAL_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String CMD = "_notify-synch";
    public static final String TEST_AT = "qI0Plnqj0cSJuH7TKx6FwoU0ZIwispQWQi08zekoCZaJhcbe5YMWwX-1ibS";
    public static final String TEST_ACCT = "hall-facilitator@hall-services.de";

    private final Map<String, String> map;
    private final Profil profil;

    public PaypalPaymentChecker(String tx, Profil profil)
    {
        this.profil = profil;
        map = new HashMap<String, String>();
        map.put("cmd", CMD);
        map.put("tx", tx); // "6LH559390U430214T"
        map.put("at", profil.getPaypalAT());
    }

    public OrderStatus hasClientPaid()
    {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            url = new URL(profil.getPaypalURL());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

            Set keys = map.keySet();
            Iterator keyIter = keys.iterator();
            String content = "";
            for (int i = 0; keyIter.hasNext(); i++)
            {
                Object key = keyIter.next();
                if (i != 0)
                {
                    content += "&";
                }
                content += key + "=" + URLEncoder.encode(map.get(key), "UTF-8");
            }
            out.writeBytes(content);

            connection.setReadTimeout(60 * 1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString().contains("SUCCESS") ? OrderStatus.PAID : OrderStatus.FAILED;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // TODO Mailer
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
        return OrderStatus.FAILED;
    }
}