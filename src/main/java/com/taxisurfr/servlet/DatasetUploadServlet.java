package com.taxisurfr.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.AgentManager;
import com.taxisurfr.server.RouteServiceManager;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.ArugamImage;
import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.server.entity.Route;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.common.io.ByteStreams;
import com.taxisurfr.server.BookingServiceManager;
import com.taxisurfr.server.ContractorManager;
import com.taxisurfr.server.ImageManager;
import com.taxisurfr.server.RatingManager;
import com.taxisurfr.server.entity.Contractor;
import com.taxisurfr.server.entity.Rating;

public class DatasetUploadServlet extends HttpServlet
{
    public static final Logger log = Logger.getLogger(LoginServlet.class.getName());

    private static final long serialVersionUID = 1L;
    ImageManager imageManager = new ImageManager();
    BookingServiceManager bookingServiceManager = new BookingServiceManager();
    RouteServiceManager routeServiceManager = new RouteServiceManager();
    ContractorManager contractorManager = new ContractorManager();
    AgentManager agentManager = new AgentManager();
    RatingManager ratingManager = new RatingManager();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        try
        {
            ObjectifyService.begin();
            ServletFileUpload upload = new ServletFileUpload();
            res.setContentType("text/plain");

            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext())
            {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();

                if (item.isFormField())
                {
                    log.warning("Got a form field: " + item.getFieldName());
                }
                else
                {
                    log.warning("Got an uploaded file: " + item.getFieldName() +
                            ", name = " + item.getName());

                    String dataset = new String(ByteStreams.toByteArray(stream));
//
//                    bookingServiceManager.importDataset(dataset, Booking.class);
//                    ratingManager.importDataset(dataset, Rating.class);
//                    imageManager.importDataset(dataset, ArugamImage.class);
                    routeServiceManager.importDataset(dataset, Route.class);
                    contractorManager.importDataset(dataset, Contractor.class);
                    agentManager.importDataset(dataset, Agent.class);
                }
            }
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }
}