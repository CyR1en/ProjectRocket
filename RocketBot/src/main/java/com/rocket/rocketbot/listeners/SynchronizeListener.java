package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.events.SynchronizeEvent;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.managers.GuildController;
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
        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bb);
        try {
            out.writeUTF("Forward");
            out.writeUTF(event.getProxiedPlayer().getServer().getInfo().getName());
            out.writeUTF("r-sync");
            out.writeUTF(event.getProxiedPlayer().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.getProxiedPlayer().getServer().sendData("BungeeCord", bb.toByteArray());
    }

    @EventHandler
    public void handleSynchronize(SynchronizeEvent event) {
        ProxiedPlayer pp = event.getProxiedPlayer();
        //LuckPermsApi api = LuckPerms.getApi();
        String group = (String) pp.getGroups().toArray()[0];
        getRocketBot().getBot().getJda().getGuilds().forEach(g -> {
            Member m = g.getMemberById(event.getUnifiedUser().getDUser().getUser().getId());
            GuildController controller = g.getController();
            controller.createRole().setName(group).queue(r -> controller.addSingleRoleToMember(m, r).queue());
            controller.setNickname(m, pp.getDisplayName()).queue();
        });
    }
}
