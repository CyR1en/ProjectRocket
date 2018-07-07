package com.rocket.rocketbot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.rocket.rocketbot.accountSync.Authentication.AuthManager;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.accountSync.listener.UserConnectionListener;
import com.rocket.rocketbot.commands.minecraftCommands.SyncConfirm;
import com.rocket.rocketbot.commands.minecraftCommands.Synchronize;
import com.rocket.rocketbot.configuration.SConfig;
import com.rocket.rocketbot.entity.Broadcaster;
import com.rocket.rocketbot.listeners.DeSyncListener;
import com.rocket.rocketbot.listeners.PPConnect;
import com.rocket.rocketbot.listeners.SynchronizeListener;
import com.rocket.rocketbot.localization.Locale;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    @Getter Broadcaster broadcaster;

    @Override
    public void onEnable() {
        config = new SConfig(this);
        locale = new Locale(this);
        eventWaiter = new EventWaiter();
        bot = new Bot(this, eventWaiter);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        authManager = new AuthManager();
        instance = this;
        broadcaster = new Broadcaster(this);
        Database.load();

        registerCommands();
        registerListener();
        registerChannel();
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
        getProxy().getPluginManager().registerListener(this, new DeSyncListener(this));
        getProxy().getPluginManager().registerListener(this, new PPConnect(this));
    }

    private void registerChannel() {
        getProxy().registerChannel("Return");
    }

    public void reload() {
        getConfig().reload();
        broadcaster.loadChannels();
    }

    public List<TextChannel> findValidTextChannels(List<String> tcID) {
        List<TextChannel> out = new ArrayList<>();
        tcID.forEach((s) -> {
            if (!s.isEmpty() && bot.getJda() != null) {
                if(StringUtils.isNumeric(s)) {
                    TextChannel tc = bot.getJda().getTextChannelById(s);
                    if (tc != null)
                        out.add(tc);
                } else {
                    List<Guild> guilds = bot.getJda().getGuilds();
                    guilds.forEach(g -> g.getTextChannels().forEach(tc -> {
                        if(tc.getName().equals(s))
                            out.add(tc);
                    }));
                }
            }
        });
        return out;
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

    public void sendToBukkit(String channel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("Return", stream.toByteArray());
    }
}
