package com.taxisurfr.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taxisurfr.server.ImageManager;

public class ImageServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    ImageManager manager = new ImageManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Long imageId = null;
        try
        {
            imageId = Long.parseLong(req.getParameter("image"));

        }
        catch (Exception ex)
        {

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
                e1.printStackTrace();
            }
        }
    }
}
