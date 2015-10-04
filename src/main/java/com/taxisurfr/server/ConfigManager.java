package com.taxisurfr.server;

import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.Config;
import com.taxisurfr.server.entity.Profil;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ConfigManager extends Manager
{
    public ConfigManager()
    {
        ObjectifyService.register(Config.class);
    }

    public void createTestConfig()
    {
        Profil profil = new Profil();
        profil.setStripePublishable("pk_test_rcKuNpP9OpTri7twmZ77UOI5");
        profil.setStripeSecret("sk_test_TCIbuNPlBRe4VowPhqekTO1L");
        profil.setName("test");
        profil.setMonitorMobile(491709025959L);
        Config config = new Config();
        config.setProfil("test");
        ofy().save().entity(profil).now();
        ofy().save().entity(config).now();
    }

    public Config getConfig()
    {
        return ofy().load().type(Config.class).list().get(0);
    }
}
