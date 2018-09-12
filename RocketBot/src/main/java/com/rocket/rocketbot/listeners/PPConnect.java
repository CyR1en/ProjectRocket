package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        ProxyServer.getInstance().getScheduler().runAsync(getRocketBot(), () -> {
            if (event.getTag().equalsIgnoreCase("BungeeCord")) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                try {
                    String channel = in.readUTF();
                    if (channel.equals("RJGQuery")) {
                        String group = in.readUTF();
                        String name = in.readUTF();
                        ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(name);
                        if(pp == null)
                            return;
                        if (!group.equals("null")) {
                            if (!checkRole(pp, group)) {
                                getRocketBot().getLogger().info("- Found de-synchronized group for " + pp);
                                String key = pp.getName();
                                ResultSet row = getRocketBot().getDb().getRowByName(key);
                                row.next();
                                String gr = row.getString("mc_group");
                                getRocketBot().getBot().removeRole(gr, pp);
                                getRocketBot().getBot().handleRole(group, pp);
                                getRocketBot().getDb().updateGroup(pp.getName(), group);
                                getRocketBot().getLogger().info("- Re-synchronized group for " + pp);
                            }
                        }
                    }
                } catch (IOException | SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private boolean checkRole(ProxiedPlayer pp, String group) {
        try {
            getRocketBot().getLogger().info("- Checking group de-synchronization for " + pp);
            ResultSet row = getRocketBot().getDb().getRowByName(pp.getName());
            if (row.next()) {
                String arg = row.getString("mc_group");
                if (arg == null) {
                    getRocketBot().getLogger().info("- Group data not found for " + pp);
                    return true; //return true to skip logic
                }
                if (arg.equals("Not Synced Yet")) {
                    getRocketBot().getLogger().info("- " + pp + " is not synchronized");
                    return true; //return true to skip logic
                }
                getRocketBot().getLogger().info(String.format("Evaluating: %s == %s", arg, group));
                return arg.equals(group);
            } else {
                getRocketBot().getLogger().info("- Data not found for " + pp);
                return true; //return true to skip logic
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
