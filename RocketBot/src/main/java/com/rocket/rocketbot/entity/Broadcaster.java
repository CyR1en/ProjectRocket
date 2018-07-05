package com.rocket.rocketbot.entity;

import com.rocket.rocketbot.RocketBot;
import lombok.Getter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Broadcaster {

    private RocketBot rocketBot;
    @Getter private List<TextChannel> registeredChannels;

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
                        message, Messenger.ResponseLevel.INFO, odt, c);
                tc.sendMessage(me).queue();
            } else {
                Message m = new MessageBuilder().append(message).build();
                tc.sendMessage(m).queue();
            }
        });
    }

    public void sendBroadcastToAllServer(String message) {
        ProxyServer.getInstance().getPlayers().forEach(p ->  {
            TextComponent prefix = new TextComponent("[RocketBot] ");
            prefix.setColor(ChatColor.GOLD);
            TextComponent fMsg = new TextComponent(message);
            fMsg.setColor(ChatColor.DARK_GREEN);

            TextComponent tcMsg = new TextComponent();
            tcMsg.addExtra(prefix);
            tcMsg.addExtra(new TextComponent(message));
            p.sendMessage(tcMsg);
        });
    }

    public void loadChannels() {
        List<String> sChannels = rocketBot.getConfig().getTextChannels();
        rocketBot.findValidTextChannels(sChannels).forEach(tc -> {
            registeredChannels.add(tc);
            rocketBot.getLogger().info(String.format("- registered %s", tc.toString()));
        });
    }
}
