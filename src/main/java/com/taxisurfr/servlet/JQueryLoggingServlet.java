package com.taxisurfr.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.taxisurfr.server.*;
import com.taxisurfr.server.entity.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

public class JQueryLoggingServlet extends HttpServlet
{
    public static final Logger log = Logger.getLogger(JQueryLoggingServlet.class.getName());
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ObjectMapper mapper = new ObjectMapper();
    private BodyReader bodyReader = new BodyReader();

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException
    {
        String body = bodyReader.getBody(request);
        JsonNode actualObj = mapper.readTree(body);
        JsonNode message = actualObj.get("message");
        log.severe(message.asText());
        log.severe(actualObj.get("stackTrace").asText());

    }
}
