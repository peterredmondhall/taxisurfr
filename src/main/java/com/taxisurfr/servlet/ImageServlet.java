package com.taxisurfr.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.ImageManager;

public class ImageServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static final Logger log = Logger.getLogger(ImageServlet.class.getName());

    ImageManager manager = new ImageManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        ObjectifyService.begin();
        String imageStr = req.getParameter("image");
        log.info("doGet:"+imageStr);
        Long imageId = null;
        try
        {
            imageId = Long.parseLong(imageStr);

        }
        catch (Exception ex)
        {
            log.severe("could not parse image with id:"+imageStr);
        }
        if (imageId != null)
        {
            byte[] bytes = null;
            try
            {
                bytes = manager.getImage(imageId);

                resp.setContentType("application/png");
                resp.setContentLength(bytes.length);

                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                OutputStream out = resp.getOutputStream();

                // Copy the contents of the file to the output stream
                byte[] buf = new byte[1024];

                int count = 0;
                while ((count = in.read(buf)) >= 0)
                {
                    out.write(buf, 0, count);
                }
                in.close();
                out.close();
            }
            catch (Exception e1)
            {
                log.severe("doGet:image "+e1.getMessage());
            }
        }
    }
}
