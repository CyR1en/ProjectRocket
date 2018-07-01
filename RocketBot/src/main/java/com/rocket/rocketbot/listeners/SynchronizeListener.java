package com.rocket.rocketbot.listeners;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import com.rocket.rocketbot.events.SynchronizeEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.List;

public class SynchronizeListener extends SListener {

    public SynchronizeListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void sendGroupQuery(SynchronizeEvent event) {
        ServerInfo server = event.getProxiedPlayer().getServer().getInfo();
        sendToBukkit("GQuery", event.getProxiedPlayer().getUniqueId().toString(), server);
        getRocketBot().getLogger().info(String.format("[%s] Ping-1!", this.getClass().getSimpleName()));
    }

    @EventHandler
    public void readQueryResponse(PluginMessageEvent event) {
        getRocketBot().getLogger().info("Event Tag: " + event.getTag());
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("RQuery")) {
                    System.out.println(event.getSender().toString());
                    ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(event.getReceiver().toString());
                    String content = in.readUTF();
                    ServerInfo server = pp.getServer().getInfo();
                    if (!content.equals("null"))
                        handleRole(content, pp);
                    sendToBukkit("command", "", server);
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

    private void handleRole(String group, ProxiedPlayer p) {
        if (group == null)
            return;
        List<Guild> guilds = getRocketBot().getBot().getJda().getGuilds();
        for (Guild g : guilds) {
            String id = SimplifiedDatabase.get(p.getUniqueId().toString());
            Member m = g.getMemberById(id);
            if(m == null)
                continue;
            GuildController controller = g.getController();
            if(PermissionUtil.canInteract(g.getMember(g.getJDA().getSelfUser()), m)) {
                if (roleExists(g, group))
                    g.getRolesByName(group, true).forEach(r -> controller.addSingleRoleToMember(m, r).queue());
                else
                    controller.createRole().setName(group).queue(r -> controller.addSingleRoleToMember(m, r).queue());
                controller.setNickname(m, p.getName()).queue();
            } else {
                String message = String.format("The bot cannot modify the role or nickname for %s because %s has a higher or equal highest!", m.getEffectiveName());
                getRocketBot().getLogger().warning(message);
            }
        }
    }

    private boolean roleExists(Guild g, String role) {
        List<Role> roles = FinderUtil.findRoles(role, g);
        return roles.size() > 0;
    }

    private void sendToBukkit(String channel, String message, ServerInfo server) {
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
