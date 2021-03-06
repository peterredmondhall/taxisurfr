package com.taxisurfr.servlet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class RecommendedServlet extends HttpServlet
{

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        ObjectifyService.begin();
        load(response);
    }

    private void load(HttpServletResponse response) throws IOException
    {
        URL url = getServletContext().getResource("/recommendation.html");
        String text = Resources.toString(url, Charsets.UTF_8);
        text = text.substring(text.indexOf("<html"), text.length() - 1);
        PrintWriter writer = response.getWriter();
        writer.print(text);
        writer.close();
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        load(response);
    }
}
