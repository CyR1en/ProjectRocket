package com.rocket.rocketbot.entity;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.rocket.rocketbot.RocketBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.md_5.bungee.api.ProxyServer;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Broadcaster {

    private RocketBot rocketBot;
    private List<TextChannel> registeredChannels;

    public Broadcaster(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
        registeredChannels = new ArrayList<>();
        loadChannels();
    }

    public void sendBroadcastToAll(String message, boolean embedded) {
        sendBroadcastToAllServer(message);
        sendBroadcastToDiscord(message, embedded);
    }

    public void sendBroadcastToDiscord(String message, boolean embedded) {
        registeredChannels.forEach(tc -> {
            if(embedded) {
                OffsetDateTime odt = OffsetDateTime.now();
                User selfUser = RocketBot.getInstance().getBot().getJda().getSelfUser();
                Color c = tc.getGuild().getMember(selfUser).getColor();
                MessageEmbed me = Messenger.embedMessage(rocketBot.getBot().getJda(),
                        message, Messenger.ResponseLevel.SUCCESS, odt, c);
                tc.sendMessage(me).queue();
            } else {
                Message m = new MessageBuilder().append(message).build();
                tc.sendMessage(m).queue();
            }
        });
    }

    public void sendBroadcastToAllServer(String message) {
        ProxyServer.getInstance().getPlayers().forEach(p -> p.chat(message));
    }

    public void loadChannels() {
        List<String> sChannels = rocketBot.getConfig().getTextChannels();
        List<Guild> guilds = rocketBot.getBot().getJda().getGuilds();
        sChannels.forEach(s -> guilds.forEach(g ->
                registeredChannels.addAll(FinderUtil.findTextChannels(s, g))));
    }
}
