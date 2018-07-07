package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PPConnect extends SListener {

    public PPConnect(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        getRocketBot().getLogger().info("POST LOGIN: " + event.getPlayer());
        ProxyServer.getInstance().getScheduler().schedule(getRocketBot(), () -> {
            ProxiedPlayer p = event.getPlayer();
            ServerInfo server = event.getPlayer().getServer().getInfo();
            getRocketBot().sendToBukkit("IGQuery", p.getUniqueId().toString(), server);
            getRocketBot().getLogger().info(String.format("IGQuery -> Backend (%s)", server.getName()));
        }, 5, TimeUnit.SECONDS);
    }

    @EventHandler
    public void handleReply(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                getRocketBot().getLogger().info(String.format("Event Channel: %s", channel));
                if (!channel.equals("RIGQuery"))
                    return;
                getRocketBot().getLogger().info("PLUGIN MESSAGE RECEIVED!");
                getRocketBot().getLogger().info("Handling role change Asynchronously");
                handle(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(PluginMessageEvent event) {
        getRocketBot().getLogger().info(String.format("Event tag: %s", event.getTag()));
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                getRocketBot().getLogger().info(String.format("Event Channel: %s", channel));
                if (channel.equals("RIGQuery")) {
                    String group = in.readUTF();
                    String uuid = in.readUTF();
                    ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                    getRocketBot().getLogger().info(String.format("R Group: %s", group));
                    if (!group.equals("null")) {
                        if (!checkRole(pp, group)) {
                            getRocketBot().getLogger().info("Role mismatch! synchronizing...");
                            String gr = Database.getJSONObject(pp.getUniqueId().toString()).getString(DataKey.MC_GROUP.toString());
                            getRocketBot().getBot().removeRole(gr, pp);
                            getRocketBot().getLogger().info("Removed " + gr + " Role!");
                            getRocketBot().getBot().handleRole(group, pp);
                            getRocketBot().getLogger().info("Role handled");
                            JSONObject jsonObject = Database.getJSONObject(pp.getUniqueId().toString());
                            jsonObject.remove(DataKey.MC_GROUP.toString());
                            jsonObject.put(DataKey.MC_GROUP.toString(), group);
                            Database.set(pp.getUniqueId().toString(), jsonObject);
                            getRocketBot().getLogger().info("Database Updated");
                            getRocketBot().getLogger().info("Finished group change handling!");
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean checkRole(ProxiedPlayer pp, String group) {
        String gr = Database.getJSONObject(pp.getUniqueId().toString()).getString(DataKey.MC_GROUP.toString());
        getRocketBot().getLogger().info(String.format("RGroup: %s | DGroup: %s", group, gr));
        if (gr == null || gr.equals("Not Synced yet")) {
            getRocketBot().getLogger().info("Skipped!");
            return true; //return true to skip logic
        }
        return gr.equals(group);
    }
}
