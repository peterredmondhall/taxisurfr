package com.taxisurfr.servlet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.googlecode.objectify.ObjectifyService;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;
import com.taxisurfr.server.ConfigManager;
import com.taxisurfr.server.StatManager;
import com.taxisurfr.server.entity.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FacebookServlet extends HttpServlet
{

    private FacebookClient facebookClient;
    public static final Logger log = Logger.getLogger(FacebookServlet.class.getName());
    private static Config config = new ConfigManager().getConfig();
    StatManager statManager = new StatManager();

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
        URL url = getServletContext().getResource("/fbcontent.htm");
        String text = Resources.toString(url, Charsets.UTF_8);
        text = text.substring(text.indexOf("<html"), text.length() - 1);
        PrintWriter writer = response.getWriter();
        writer.print(text);
        writer.close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        if (true)
        {
            load(response);
            return;
        }

        String signedRequest = request.getParameter("signed_request");
        log.log(Level.SEVERE, "XXXXXXXXX doPost signedRequest" + signedRequest);

        FacebookSignedRequest facebookSR = null;
        try
        {
            facebookSR = FacebookSignedRequest.getFacebookSignedRequest(signedRequest);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.log(Level.SEVERE, "XXXXXXXXX doPost1");

        String oauthToken = facebookSR.getOauth_token();
        log.log(Level.SEVERE, "XXXXXXXXX doPost oauthToken" + oauthToken);
        PrintWriter writer = response.getWriter();

        if (oauthToken == null)
        {
            log.log(Level.SEVERE, "XXXXXXXXX doPost2");

            response.setContentType("text/html");
            String authURL = "https://www.facebook.com/dialog/oauth?client_id="
                    + config.getFbAppKey() + "&redirect_uri=https://apps.facebook.com/1651399821757463/&scope=publish_stream,create_event";
            writer.print("<script> top.location.href='" + authURL + "'</script>");
            writer.close();

        }
        else
        {
            log.log(Level.SEVERE, "XXXXXXXXX doPost3");

            facebookClient = new DefaultFacebookClient(oauthToken);
            Connection<Post> myFeed = facebookClient.fetchConnection("taxisurfr/feed", Post.class);

            log.log(Level.SEVERE, "First item in my feed: " + myFeed.getData().get(0));

            // authorisePublish(facebookClient, response);
            //			GraphPublisherExample graphPublisherExample = new GraphPublisherExample(oauthToken);
            //			graphPublisherExample.runEverything();

            writer.print("<iframe src=\"https://taxigangsurf.appspot.com\" width=\"830\" height=\"610\"></iframe>");
            writer.close();

            //			FacebookType publishMessageResponse =
            //			        facebookClient.publish("taxisurfr/feed", FacebookType.class,
            //			          Parameter.with("message", "RestFB test"));
            //
            //			log.log(Level.SEVERE,"Published message ID: " + publishMessageResponse.getId());

            //			Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
            //			writer.print("<table><tr><th>Photo</th><th>Name</th><th>Id</th></tr>");
            //			for (List<User> myFriendsList : myFriends) {
            //
            //				for(User user: myFriendsList)
            //					writer.print("<tr><td><img src=\"https://graph.facebook.com/" + user.getId() + "/picture\"/></td><td>" + user.getName() +"</td><td>" + user.getId() + "</td></tr>");
            //
            //			}
            //			writer.print("</table>");
            //			writer.close();

        }
    }

    private void authorisePublish(FacebookClient facebookClient, HttpServletResponse response)
    {
        log.log(Level.SEVERE, "authorisePublish: ");
        PrintWriter writer;
        try
        {
            writer = response.getWriter();

            response.setContentType("text/html");
            String authURL = String.format("https://graph.facebook.com/dialog/​oauth/authorize?​client_id=$&​redirect_uri=http://www.facebook.com/​connect/login_success.html&​scope=publish_stream,​create_event", config.getFbAppSecret());

            // facebookClient.
            writer.print("<script> top.location.href='" + authURL + "'</script>");
            writer.close();
        }

        //	          Request https://graph.facebook.com/​oauth/authorize?​client_id=MY_API_KEY&​redirect_uri=http://www.facebook.com/​connect/login_success.html&​scope=publish_stream,​create_event
        //	                Facebook will redirect you to http://www.facebook.com/​connect/​login_success.html?code=MY_VERIFICATION_CODE
        //	                Request https://graph.facebook.com/​oauth/access_token?​client_id=MY_API_KEY&​redirect_uri=http://www.facebook.com/​connect/​login_success.html&​client_secret=MY_APP_SECRET&​code=MY_VERIFICATION_CODE
        //	                Facebook will respond with access_token=MY_ACCESS_TOKEN
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.log(Level.SEVERE, "error publishing: " + e.getMessage());
        }

    }

}
