package com.taxisurfr.servlet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.taxisurfr.server.AgentManager;
import com.taxisurfr.shared.model.AgentInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/*
 * Login Google user and redirect to application main page
 * 
 */
public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static final Logger log = Logger.getLogger(LoginServlet.class.getName());
    private final AgentManager userManager = new AgentManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null)
        {
            // send to Google login page
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
            return;
        }

        AgentInfo userInfo = userManager.getAgent(user.getEmail());
        if (userInfo != null)
        {
            req.getSession().setAttribute("user", user);

            resp.sendRedirect("/dashboard.html");

        }
        else
        {
            resp.getWriter().write("You are not authorized to access Admin Portal!");

        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doPost(req, resp);
    }
}
