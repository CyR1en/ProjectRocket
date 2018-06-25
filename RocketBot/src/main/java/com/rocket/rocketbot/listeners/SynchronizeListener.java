package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.events.SynchronizeEvent;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.managers.GuildController;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SynchronizeListener extends SListener {

    public SynchronizeListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void sendPluginMessage(SynchronizeEvent event) {
        ServerInfo server = event.getProxiedPlayer().getServer().getInfo();
        sendToBukkit("command", event.getProxiedPlayer().getName(), server);
    }

    @EventHandler
    public void handleSynchronize(SynchronizeEvent event) {
        ProxiedPlayer pp = event.getProxiedPlayer();
        LuckPermsApi api = LuckPerms.getApi();
        User user = api.getUser(event.getProxiedPlayer().getUniqueId());
        String group = user != null ? user.getPrimaryGroup() : null;
        if(group != null) {
            getRocketBot().getBot().getJda().getGuilds().forEach(g -> {
                Member m = g.getMemberById(event.getUnifiedUser().getDUser().getUser().getId());
                GuildController controller = g.getController();
                controller.createRole().setName(group).queue(r -> controller.addSingleRoleToMember(m, r).queue());
                controller.setNickname(m, pp.getDisplayName()).queue();
            });
        }
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
        // Note the "Return". It is the channel name that we registered in our Main class of Bungee plugin.
        server.sendData("Return", stream.toByteArray());

    }
}
