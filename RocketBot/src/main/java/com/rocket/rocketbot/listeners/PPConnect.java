package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class PPConnect extends SListener {

    public PPConnect(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void handleReply(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (!channel.equals("RJGQuery"))
                    return;
                handle(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("RJGQuery")) {
                    String group = in.readUTF();
                    String uuid = in.readUTF();
                    ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                    if (!group.equals("null")) {
                        if (!checkRole(pp, group)) {
                            String key = pp.getName();
                            String gr = Database.getJSONObject(key).getString(DataKey.MC_GROUP.toString());
                            getRocketBot().getBot().removeRole(gr, pp);
                            getRocketBot().getBot().handleRole(group, pp);
                            JSONObject jsonObject = Database.getJSONObject(key);
                            jsonObject.remove(DataKey.MC_GROUP.toString());
                            jsonObject.put(DataKey.MC_GROUP.toString(), group);
                            Database.set(key, jsonObject);
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean checkRole(ProxiedPlayer pp, String group) {
        String gr = Database.getJSONObject(pp.getName()).getString(DataKey.MC_GROUP.toString());
        if (gr == null || gr.equals("Not Synced yet"))
            return true; //return true to skip logic
        return gr.equals(group);
    }
}
