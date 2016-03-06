package com.taxisurfr.server;

import com.taxisurfr.server.entity.Route;
import com.taxisurfr.server.entity.SessionStat;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.model.StatInfo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.googlecode.objectify.ObjectifyService.register;

public class StatManager extends Manager
{
    private static final Logger logger = Logger.getLogger(StatManager.class.getName());
    public static String newline = System.getProperty("line.separator");

    public StatManager()
    {
        register(SessionStat.class);

    }
    public void updateSessionStat(StatInfo statInfo)
    {
        SessionStat sessionStat = ofy().load().type(SessionStat.class).filter("ip", statInfo.getIp()).first().now();
        if (sessionStat != null)
        {
            switch (statInfo.getUpdate())
            {
                case TYPE:
                    sessionStat.setType(statInfo.getDetail());
                    break;
                case ROUTE:
                    sessionStat.setRoute(statInfo.getDetail());
                    break;
            }
            ofy().save().entity(sessionStat).now();
        }
        else
        {
            logger.log(Level.SEVERE, "not session found for ip " + statInfo.getIp());
        }
     }

    public void report()
    {
        List<SessionStat> list = getAll(SessionStat.class);
        String report = "sessions:" + list.size();
        for (SessionStat stat : list)
        {
            report += "<br> src=" + stat.getSrc() + "  country=" + stat.getCountry() + "  type=" + stat.getType();

        }

        Mailer.sendReport(report);
        deleteAll(SessionStat.class);

    }

    @Deprecated
    public StatInfo createSessionStatFromInfo(StatInfo statInfo)
    {
        SessionStat sessionStat = ofy().load().type(SessionStat.class).filter("ip =", statInfo.getIp()).first().now();
        if (sessionStat == null)
        {
            sessionStat = SessionStat.getSessionStat(statInfo);
            ofy().save().entity(sessionStat).now();
        }
        return sessionStat.getInfo();
    }

    public SessionStat createSessionStat(SessionStat ss) {
        SessionStat sessionStat = ofy().load().type(SessionStat.class).filter("reference =", ss.getReference()).first().now();
        if (sessionStat != null){
            sessionStat.incInteractions();
            ofy().save().entity(sessionStat).now();
            return sessionStat;
        }else {
            Long id = ofy().save().entity(ss).now().getId();
            return ofy().load().type(SessionStat.class).id(id).now();
        }
    }

    public SessionStat addRoute(String reference, Route route, String start, String end) {
        SessionStat sessionStat = ofy().load().type(SessionStat.class).filter("reference =", reference).first().now();
        if (sessionStat != null)
        {
            sessionStat.setRoute(start+" to "+end);
            ofy().save().entity(sessionStat).now();
        }
        return sessionStat;
    }

}
