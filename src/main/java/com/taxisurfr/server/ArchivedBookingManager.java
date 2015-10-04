package com.taxisurfr.server;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.ArchivedBooking;

import javax.annotation.Nullable;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ArchivedBookingManager extends Manager
{
    private static final Logger logger = Logger.getLogger(ArchivedBookingManager.class.getName());

    public ArchivedBookingManager()
    {

        ObjectifyService.register(ArchivedBooking.class);
    }

    public String getMailingList()
    {
        List<ArchivedBooking> archivedBookingList = ofy().load().type(ArchivedBooking.class).list();
        return join(archivedBookingList);
    }

    public String join(List<ArchivedBooking> archivedBookingList)
    {
        ImmutableList<String> strings = FluentIterable.from(archivedBookingList).transform(new Function<ArchivedBooking, String>()
        {
            @Nullable @Override public String apply(ArchivedBooking archivedBooking)
            {
                return archivedBooking.getEmail() + "," + archivedBooking.getName();
            }
        }).toList();
        return Joiner.on("\r\n").join(strings);
    }

}
