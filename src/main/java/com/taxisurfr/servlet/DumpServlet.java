package com.taxisurfr.servlet;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.*;
import com.taxisurfr.server.entity.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

public class DumpServlet extends HttpServlet
{
    public static final Logger log = Logger.getLogger(DumpServlet.class.getName());
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    RouteServiceManager routeServiceManager = new RouteServiceManager();
    ImageManager imageManager = new ImageManager();
    BookingServiceManager bookingServiceManager = new BookingServiceManager();
    RatingManager ratingManager = new RatingManager();

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        ObjectifyService.begin();

        if (!bookingServiceManager.getMaintenceAllowed())
        {
            log.info("not allowed");
            return;
        }
        String bookings = bookingServiceManager.dump(Booking.class);
        String ratings = ratingManager.dump(Rating.class);
        String routes = routeServiceManager.dump(Route.class);
        String images = imageManager.dump(ArugamImage.class);
        String contractors = new ContractorManager().dump(Contractor.class);
        String agents = new AgentManager().dump(Agent.class);
        List<String> outputs = ImmutableList.of(bookings, ratings, images, routes, contractors, agents);
        int contentLength = 0;
        for (String output : outputs)
        {
            contentLength += output.length();

        }
        try
        {
            resp.setContentType("application/txt");
            resp.addHeader("Content-Disposition", "inline; filename=\"dataset.txt\"");
            resp.setContentLength(contentLength);

            OutputStream out = resp.getOutputStream();

            for (String output : outputs)
            {
                ByteArrayInputStream in = new ByteArrayInputStream(output.getBytes());
                // Copy the contents of the file to the output stream
                byte[] buf = new byte[1024];

                int count = 0;
                while ((count = in.read(buf)) >= 0)
                {
                    out.write(buf, 0, count);
                }
                in.close();
            }
            out.close();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }

    }
}
