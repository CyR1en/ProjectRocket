package com.rocket.rocketbot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.rocket.rocketbot.accountSync.Authentication.AuthManager;
import com.rocket.rocketbot.accountSync.listener.UserConnectionListener;
import com.rocket.rocketbot.commands.minecraftCommands.SyncConfirm;
import com.rocket.rocketbot.commands.minecraftCommands.Synchronize;
import com.rocket.rocketbot.configuration.SConfig;
import com.rocket.rocketbot.listeners.SynchronizeListener;
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
        authManager = new AuthManager();
        locale = new Locale(this);
        instance = this;

        registerCommands();
        registerListener();
    }

    @Override
    public void onDisable() {
        bot.shutdown();
    }

    private void registerCommands() {
        getProxy().getPluginManager().registerCommand(this, new Synchronize(this));
        getProxy().getPluginManager().registerCommand(this, new SyncConfirm(this));
    }

    private void registerListener() {
        getProxy().getPluginManager().registerListener(this, new SynchronizeListener(this));
        getProxy().getPluginManager().registerListener(this, new UserConnectionListener(this));
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
