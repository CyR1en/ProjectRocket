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
import java.util.UUID;

public class SynchronizeListener extends SListener {

    public SynchronizeListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void sendGroupQuery(SynchronizeEvent event) {
        ServerInfo server = event.getProxiedPlayer().getServer().getInfo();
        getRocketBot().sendToBukkit("GQuery", event.getProxiedPlayer().getUniqueId().toString(), server);
        getRocketBot().getLogger().info(String.format("[%s] Ping-1!", this.getClass().getSimpleName()));
    }

    @EventHandler
    public void readQueryResponse(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("RQuery")) {
                    String content = in.readUTF();
                    String uuid = in.readUTF();
                    ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                    ServerInfo server = pp.getServer().getInfo();
                    if (!content.equals("null"))
                        getRocketBot().getBot().handleRole(content, pp);
                    getRocketBot().sendToBukkit("command", "", server);
                    getRocketBot().getLogger().info(String.format("[%s] Ping-2!", this.getClass().getSimpleName()));
                    String message = RocketBot.getLocale().getTranslatedMessage("sync.broadcast")
                            .f(pp.getName());
                    getRocketBot().getBroadcaster().sendBroadcastToAll(message, true);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
