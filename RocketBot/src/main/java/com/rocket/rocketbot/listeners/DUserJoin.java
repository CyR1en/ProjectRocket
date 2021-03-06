package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.entity.Messenger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;

import java.time.OffsetDateTime;
import java.util.List;

public class DUserJoin extends ListenerAdapter {

    private RocketBot rocketBot;

    public DUserJoin(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        rocketBot.getLogger().info(String.format("New member joined (%s)", event.getMember().getEffectiveName()));
        List<TextChannel> tcs = rocketBot.getBroadcaster().getRegisteredSyncChannels();
        tcs.forEach(tc -> {
            if(tc.getGuild().equals(event.getGuild())) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setThumbnail(event.getMember().getUser().getAvatarUrl());
                String fW = RocketBot.getLocale().getTranslatedMessage("discord.welcome-w").f(event.getMember().getEffectiveName());
                eb.setDescription(ChatColor.stripColor(fW));
                String fHeader = RocketBot.getLocale().getTranslatedMessage("discord.welcome-fh").finish();
                String fContent = RocketBot.getLocale().getTranslatedMessage("discord.welcome-fc").finish();
                eb.addField(ChatColor.stripColor(fHeader), ChatColor.stripColor(fContent), false);
                MessageEmbed mE = Messenger.embedMessage(event.getJDA().getSelfUser(), eb.build(),
                        Messenger.ResponseLevel.INFO, OffsetDateTime.now(), event.getGuild().getMember(event.getJDA().getSelfUser()).getColor());
                tc.sendMessage(mE).queue();
            }
        });
    }

}
