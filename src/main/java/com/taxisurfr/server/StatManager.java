package com.taxisurfr.server;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.taxisurfr.server.entity.SessionStat;
import com.taxisurfr.server.util.Mailer;
import com.taxisurfr.shared.model.StatInfo;

public class StatManager extends Manager
{
    private static final Logger logger = Logger.getLogger(StatManager.class.getName());
    public static String newline = System.getProperty("line.separator");

    public void updateSessionStat(StatInfo statInfo)
    {
        try
        {

            EntityManager em = getEntityManager();
            try
            {
                SessionStat sessionStat = (SessionStat) em.createQuery("select t from SessionStat t where ident=" + statInfo.getIdent()).getSingleResult();

                switch (statInfo.getUpdate())
                {
                    case TYPE:
                        sessionStat.setType(statInfo.getDetail());
                        break;
                    case ROUTE:
                        sessionStat.setRoute(statInfo.getDetail());
                        break;
                }
                em.persist(sessionStat);
            }
            catch (Exception e)
            {
                logger.info("problem writing statInfo" + e.getMessage());
            }
            finally
            {
                em.close();
            }
        }
        catch (Exception ex)
        {
            logger.info("problem writing statInfo" + ex.getMessage());
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

    public void createSessionStat(StatInfo statInfo)
    {
        EntityManager em = getEntityManager();
        try
        {
            SessionStat stat = SessionStat.getSessionStat(statInfo);
            em.persist(stat);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            em.close();
        }
    }
}
