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
import com.rocket.rocketbot.listeners.LBBroadcast;
import com.rocket.rocketbot.listeners.PPConnect;
import com.rocket.rocketbot.listeners.SynchronizeListener;
import com.rocket.rocketbot.localization.Locale;
import com.rocket.rocketbot.utils.Finder;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

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
        getProxy().getPluginManager().registerListener(this, new LBBroadcast(this));
    }

    private void registerChannel() {
        getProxy().registerChannel("Return");
    }

    public void reload() {
        getConfig().reload();
        broadcaster.loadChannels();
    }

    public List<TextChannel> findValidTextChannels(List<String> tcID) {
        List<Channel> generic = Finder.findValidChannels(TextChannel.class, tcID);
        return generic.stream().filter(Objects::nonNull).map(e -> (TextChannel) e).collect(Collectors.toList()) ;
    }

    public List<VoiceChannel> findValidVoiceChannel(List<String> vcID) {
        List<Channel> generic = Finder.findValidChannels(VoiceChannel.class, vcID);
        return generic.stream().filter(Objects::nonNull).map(e -> (VoiceChannel) e).collect(Collectors.toList()) ;
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
