package com.taxisurfr.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.common.io.ByteStreams;
import com.taxisurfr.server.ImageManager;

public class ImageUploadServlet extends HttpServlet
{
    public static final Logger log = Logger.getLogger(LoginServlet.class.getName());

    private static final long serialVersionUID = 1L;
    ImageManager imageManager = new ImageManager();

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

                    byte[] image = ByteStreams.toByteArray(stream);
                    Long id = imageManager.addImage(image);

                    ServletOutputStream os = res.getOutputStream();
                    os.print("***" + id + "***");
                }
            }
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }
}