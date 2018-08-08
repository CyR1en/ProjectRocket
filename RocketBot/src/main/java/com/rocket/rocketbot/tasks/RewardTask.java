package com.rocket.rocketbot.tasks;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.utils.Finder;
import com.rocket.rocketbot.utils.ListUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RewardTask implements Runnable {

    private RocketBot rocketBot;
    private final List<ProxiedPlayer> participants;
    private final List<VoiceChannel> rewardChannel;
    private final int min;

    public RewardTask(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
        List<String> sVC = rocketBot.getConfig().getRewardChannels();
        this.rewardChannel = rocketBot.findValidVoiceChannel(sVC);
        this.min = rocketBot.getConfig().getMinCount();
        participants = new ArrayList<>();
    }

    @Override
    public void run() {
        Collection<ProxiedPlayer> players = rocketBot.getProxy().getPlayers();
        players.forEach(proxiedPlayer -> rewardChannel.forEach(rc -> {
            Member m = Finder.findUserInDatabase(proxiedPlayer) == null ? null :
                    rc.getGuild().getMember(Finder.findUserInDatabase(proxiedPlayer));
            if(m != null && rc.getMembers().contains(m))
                participants.add(proxiedPlayer);
        }));
        if(participants.size() >= min) {
            ProxiedPlayer winner = ListUtil.chooseRandom(participants);
            ServerInfo server = winner.getServer().getInfo();
            rocketBot.sendToBukkit("RRCommand", "", server);
            String rCs = StringUtils.join(rewardChannel, ",").replaceAll("\\s([^,]+)$", " and $1");
            String message = RocketBot.getLocale().getTranslatedMessage("raffle.win").f(winner, rCs);
            rocketBot.getBroadcaster().sendBroadcastToAll(message, true);
        }
        participants.clear();

    }
}