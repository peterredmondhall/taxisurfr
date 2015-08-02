package com.taxisurfr.server;

import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.Booking;
import com.taxisurfr.server.entity.Config;

public class ConfigManager extends Manager
{
    public ConfigManager()
    {
        ObjectifyService.register(Config.class);
    }

    public Config getConfig()
    {
throw new RuntimeException();
        //return Config.getConfig(getEntityManager());
    }
}
