package com.taxisurfr.server;

import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.SessionStat;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.model.StatInfo;

import java.util.List;
import java.util.logging.Logger;

public class StatManager extends Manager
{
    private static final Logger logger = Logger.getLogger(StatManager.class.getName());
    public static String newline = System.getProperty("line.separator");

    public StatManager()
    {
        ObjectifyService.register(SessionStat.class);

    }
    public void updateSessionStat(StatInfo statInfo)
    {
        SessionStat sessionStat = ObjectifyService.ofy().load().type(SessionStat.class).filter("ip", statInfo.getIp()).first().now();
        switch (statInfo.getUpdate())
        {
            case TYPE:
                sessionStat.setType(statInfo.getDetail());
                break;
            case ROUTE:
                sessionStat.setRoute(statInfo.getDetail());
                break;
        }
            ObjectifyService.ofy().save().entity(sessionStat).now();
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

    public StatInfo createSessionStat(StatInfo statInfo)
    {
        SessionStat sessionStat = ObjectifyService.ofy().load().type(SessionStat.class).filter("ip =", statInfo.getIp()).first().now();
        if (sessionStat == null)
        {
            sessionStat = SessionStat.getSessionStat(statInfo);
            ObjectifyService.ofy().save().entity(sessionStat).now();
        }
        return sessionStat.getInfo();
    }
}
