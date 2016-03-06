package com.taxisurfr.server;

import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.Config;
import com.taxisurfr.server.entity.Profil;
import com.taxisurfr.shared.model.AgentInfo;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ConfigManager extends Manager {
    public ConfigManager() {
        ObjectifyService.register(Config.class);
    }

    public void createTestConfig() {
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
        Config config = null;
        List<Config> list = ofy().load().type(Config.class).list();
        if (!list.isEmpty()){
            config = list.get(0);
        } else
        if (SystemProperty.environment.value() !=
                SystemProperty.Environment.Value.Production ) {
            createTestConfig();
            config = getConfig();
        }
        return config;
    }
}
