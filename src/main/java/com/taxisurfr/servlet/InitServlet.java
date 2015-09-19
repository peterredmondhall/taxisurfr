package com.taxisurfr.servlet;

import com.taxisurfr.server.BookingServiceManager;
import com.taxisurfr.server.CurrencyManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class InitServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static final Logger log = Logger.getLogger(InitServlet.class.getName());

    BookingServiceManager bookingServiceManager = new BookingServiceManager();
    CurrencyManager currencyManager = new CurrencyManager();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
        bookingServiceManager.createAgentWithRoutes("test@example.com");
    }

}
