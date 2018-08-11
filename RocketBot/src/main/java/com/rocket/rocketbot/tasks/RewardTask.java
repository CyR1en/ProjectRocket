package com.rocket.rocketbot.tasks;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.utils.Finder;
import com.rocket.rocketbot.utils.ListUtil;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        rocketBot.getLogger().info("- Raffle has started!");
        Collection<ProxiedPlayer> players = rocketBot.getProxy().getPlayers();
        rewardChannel.forEach(rewardChannel -> participants.addAll(players.stream().filter(p -> {
            User u = Finder.findUserInDatabase(p);
            if(u == null) return false;
            Member m = rewardChannel.getGuild().getMember(u);
            return rewardChannel.getMembers().stream().anyMatch(m1 -> m1.getUser().getId().equals(m.getUser().getId()));
        }).collect(Collectors.toList())));
        if(participants.size() >= min) {
            ProxiedPlayer winner = ListUtil.chooseRandom(participants);
            ServerInfo server = winner.getServer().getInfo();
            rocketBot.sendToBukkit("RRCommand", "", server);
            List<String> cName = rewardChannel.stream().map(Channel::getName).collect(Collectors.toList());
            String rCs = StringUtils.join(cName, ",").replaceAll("\\s([^,]+)$", " and $1");
            String message = RocketBot.getLocale().getTranslatedMessage("raffle.win").f(winner, rCs);
            rocketBot.getBroadcaster().sendBroadcastToAll(message, true);
        } else {
            rocketBot.getLogger().info("- There is an insufficient amount listener to do a raffle!");
        }
        participants.clear();
    }
}