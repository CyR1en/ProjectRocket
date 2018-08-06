package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class DUserListen extends ListenerAdapter {

    private RocketBot rocketBot;
    private List<VoiceChannel> voiceChannels;

    public DUserListen(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
        voiceChannels = rocketBot.findValidVoiceChannel(rocketBot.getConfig().getVoiceChannels());
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if(voiceChannels.contains(event.getChannelJoined())) {
            Member m = event.getMember();
            VoiceChannel vc = event.getChannelJoined();
            String message = RocketBot.getLocale().getTranslatedMessage("support.join").f(m.getEffectiveName(), vc.getName());
            rocketBot.getBroadcaster().sendBroadcastToStaff(message);
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if(voiceChannels.contains(event.getChannelLeft())) {
            Member m = event.getMember();
            VoiceChannel vc = event.getChannelLeft();
            String message = RocketBot.getLocale().getTranslatedMessage("support.leave").f(m.getEffectiveName(), vc.getName());
            rocketBot.getBroadcaster().sendBroadcastToStaff(message);
        }
    }
}
