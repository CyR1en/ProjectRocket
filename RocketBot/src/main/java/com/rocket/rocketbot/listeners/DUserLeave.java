package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DUserLeave extends ListenerAdapter {

    private RocketBot rocketBot;

    public DUserLeave(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(rocketBot, () -> processAsync(event));
    }

    private void processAsync(GuildMemberLeaveEvent event) {
        try {
            User user = event.getUser();
            ResultSet row = rocketBot.getDb().getRowByDID(user.getId());
            if (row.next()) {
                String name = row.getString("name");
                rocketBot.getDb().reset(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
