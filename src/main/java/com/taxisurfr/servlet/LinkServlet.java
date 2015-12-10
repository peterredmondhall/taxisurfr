package com.taxisurfr.servlet;

import com.google.common.base.Splitter;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.taxisurfr.server.RouteServiceManager;
import com.taxisurfr.server.entity.Route;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class LinkServlet extends HttpServlet {
    public static final Logger log = Logger.getLogger(LinkServlet.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RouteServiceManager routeManager = new RouteServiceManager();

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

        String returnLink = ("/");
        String pathInfo = request.getPathInfo();
        List<String> strings = Lists.newArrayList(Splitter.on('/').omitEmptyStrings().split(pathInfo));
        if (!strings.isEmpty()) {
            Route route = routeManager.getRouteFromLink(strings.get(0));

            if (route != null) {
                log.info("link to " + route.getStart() + " " + route.getEnd());
                returnLink = "/#/form/route?route=" + route.getId();
            }
        }
        //http://localhost:8080/#/form/route?route=5840605766746112
        resp.sendRedirect(returnLink);


    }
}
