package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.events.SynchronizeEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class SynchronizeListener extends SListener {

    private SynchronizeEvent evt;

    public SynchronizeListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void handleSynchronize(SynchronizeEvent event) {
        ServerInfo server = event.getProxiedPlayer().getServer().getInfo();
        sendToBukkit("GQuery", event.getProxiedPlayer().getUniqueId().toString(), server);
        evt = event;
    }

    @EventHandler
    public void getGroupQuery(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if(channel.equals("RQuery")){
                    ServerInfo server = evt.getProxiedPlayer().getServer().getInfo();
                    String group = in.readUTF();
                    handleRole(group);
                    sendToBukkit("command", "", server);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void handleRole(String group) {
        ProxiedPlayer pp = evt.getProxiedPlayer();
        if(group != null) {
            getRocketBot().getBot().getJda().getGuilds().forEach(g -> {
                Member m = g.getMemberById(evt.getUnifiedUser().getDUser().getUser().getId());
                GuildController controller = g.getController();
                if(roleExists(g, group))
                    g.getRolesByName(group, true).forEach(r -> controller.addSingleRoleToMember(m, r).queue());
                else
                    controller.createRole().setName(group).queue(r -> controller.addSingleRoleToMember(m, r).queue());
                controller.setNickname(m, pp.getName()).queue();
            });
        }
    }

    private boolean roleExists(Guild g, String role) {
        for (Role r : g.getRoles())
            if(r.getName().equalsIgnoreCase(role))
                return true;
        return false;
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
