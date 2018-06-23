package com.rocket.rocketbot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.rocket.rocketbot.accountSync.Authentication.AuthManager;
import com.rocket.rocketbot.configuration.SConfig;
import com.rocket.rocketbot.localization.Locale;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RocketBot extends Plugin {

    private static RocketBot instance;
    private static Locale locale;

    @Getter Bot bot;
    @Getter EventWaiter eventWaiter;
    @Getter private AuthManager authManager;
    @Getter private SConfig config;
    @Getter ScheduledExecutorService scheduler;

    @Override
    public void onEnable() {
        config = new SConfig(this);
        eventWaiter = new EventWaiter();
        bot = new Bot(this, eventWaiter);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        instance = this;
        locale = new Locale(this);
    }

    @Override
    public void onDisable() {
        bot.shutdown();
    }

    public static Locale getLocale() {
        if(locale == null)
            return new Locale(getInstance());
        return locale;
    }
    
    public static RocketBot getInstance() {
        if(instance == null)
            return new RocketBot();
        return instance;
    }
}
