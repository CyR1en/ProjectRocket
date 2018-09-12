package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.events.SynchronizeEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SynchronizeListener extends SListener {

    public SynchronizeListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void sendGroupQuery(SynchronizeEvent event) {
        ServerInfo server = event.getProxiedPlayer().getServer().getInfo();
        getRocketBot().sendToBukkit("GQuery", event.getProxiedPlayer().getName(), server);
    }

    @EventHandler
    public void readQueryResponse(PluginMessageEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(getRocketBot(), () -> {
            if (event.getTag().equalsIgnoreCase("BungeeCord")) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                try {
                    String channel = in.readUTF();
                    if (channel.equals("RQuery")) {
                        String content = in.readUTF();
                        String name = in.readUTF();
                        ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(name);
                        ServerInfo server = pp.getServer().getInfo();
                        if (!content.equals("null"))
                            getRocketBot().getBot().handleRole(content, pp);
                        ResultSet row = getRocketBot().getDb().getRowByName(pp.getName());
                        row.next();
                        boolean isRewarded = row.getBoolean("rewarded");
                        if(!isRewarded) {
                            getRocketBot().sendToBukkit("RCommand", pp.getName(), server);
                            getRocketBot().getDb().setBool(pp.getName(), "rewarded", true);
                        }
                        String message = isRewarded ? RocketBot.getLocale().getTranslatedMessage("sync.broadcast-2")
                                .f(pp.getName()) : RocketBot.getLocale().getTranslatedMessage("sync.broadcast-1")
                                .f(pp.getName());
                        getRocketBot().getBroadcaster().sendBroadcastToAll(message, true);
                    }
                } catch (IOException | SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
