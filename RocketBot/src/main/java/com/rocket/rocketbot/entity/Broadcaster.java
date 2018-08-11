package com.rocket.rocketbot.entity;

import com.rocket.rocketbot.RocketBot;
import lombok.Getter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Broadcaster {

    private RocketBot rocketBot;
    @Getter private List<TextChannel> registeredSyncChannels;
    @Getter private List<TextChannel> registeredBanChannels;

    public Broadcaster(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
        registeredSyncChannels = new ArrayList<>();
        registeredBanChannels = new ArrayList<>();
    }

    public void sendBroadcastToAll(String message, boolean embedded) {
        sendBroadcastToAllBackend(message);
        sendBroadcastToDiscord(message, embedded);
    }

    public void sendBroadcastToDiscord(String message, boolean embedded) {
        registeredSyncChannels.forEach(tc -> {
            if(embedded) {
                OffsetDateTime odt = OffsetDateTime.now();
                User selfUser = RocketBot.getInstance().getBot().getJda().getSelfUser();
                Color c = tc.getGuild().getMember(selfUser).getColor();
                MessageEmbed me = Messenger.embedMessage(rocketBot.getBot().getJda(),
                        message, Messenger.ResponseLevel.BROADCAST, odt, c);
                tc.sendMessage(me).queue();
            } else {
                Message m = new MessageBuilder().append(message).build();
                tc.sendMessage(m).queue();
            }
        });
    }

    public void sendBroadcastToStaff(String message) {
        List<ProxiedPlayer> staff = ProxyServer.getInstance().getPlayers()
                .stream().filter(p -> p.hasPermission("rocket.staff")).collect(Collectors.toList());
        staff.forEach(s -> sendMessageToPP(s, message));
    }

    public void sendBroadcastToAllBackend(String message) {
        ProxyServer.getInstance().getPlayers().forEach(p -> sendMessageToPP(p, message));
    }

    private void sendMessageToPP(ProxiedPlayer pp, String message) {
        TextComponent prefix = new TextComponent("[RocketBot] ");
        prefix.setColor(ChatColor.GOLD);
        TextComponent fMsg = new TextComponent(message);
        fMsg.setColor(ChatColor.DARK_GREEN);

        TextComponent tcMsg = new TextComponent();
        tcMsg.addExtra(prefix);
        tcMsg.addExtra(new TextComponent(message));
        pp.sendMessage(tcMsg);
    }

    private void sendPrivateMessage(User user, MessageEmbed m) {
        sendPrivateMessage(user, p -> p.sendMessage(m).queue());
    }
    private void sendPrivateMessage(User user, String message) {
        sendPrivateMessage(user, pc -> pc.sendMessage(message).queue());
    }

    private void sendPrivateMessage(User u, Consumer<? super PrivateChannel> consumer) {
        u.openPrivateChannel().queue(consumer, c -> rocketBot.getLogger().warning("Unable to send private message to " + c.toString()));
    }

    public void sendBanMessage(String message) {
        message = ChatColor.stripColor(message);
        MessageEmbed mb = Messenger.embedMessage(
                rocketBot.getBot().getJda(), message, Messenger.ResponseLevel.LB_BROADCAST, OffsetDateTime.now(), null);
        getRegisteredBanChannels().forEach(c -> c.sendMessage(mb).queue());
    }

    public void loadChannels() {
        registeredSyncChannels.clear();
        registeredBanChannels.clear();
        rocketBot.findValidTextChannels(rocketBot.getConfig().getSyncChannels()).forEach(tc -> {
            registeredSyncChannels.add(tc);
            rocketBot.getLogger().info(String.format("- Registered SyncChannel: %s", tc.toString()));
        });
        rocketBot.findValidTextChannels(rocketBot.getConfig().getBanChannels()).forEach(tc -> {
            registeredBanChannels.add(tc);
            rocketBot.getLogger().info(String.format("- Registered BanChannel: %s", tc.toString()));
        });
    }
}
