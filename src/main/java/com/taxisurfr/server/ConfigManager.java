package com.taxisurfr.server;

import com.taxisurfr.server.entity.Config;

public class ConfigManager extends Manager
{

    public Config getConfig()
    {
        return Config.getConfig(getEntityManager());
    }
}
