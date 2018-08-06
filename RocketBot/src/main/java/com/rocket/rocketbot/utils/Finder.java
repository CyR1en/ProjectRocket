package com.rocket.rocketbot.utils;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import net.dv8tion.jda.core.entities.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Finder {

    public static Member findMember(String query) {
        List<Guild> guilds = RocketBot.getInstance().getBot().getJda().getGuilds();
        List<Member> members;
        for (Guild guild : guilds) {
            members = FinderUtil.findMembers(query, guild);
            if (members.size() > 0)
                return members.get(0);
        }
        return null;
    }

    public static Member findMember(String query, Guild guild) {
        List<Member> members = FinderUtil.findMembers(query, guild);
        if (members.size() > 0)
            return members.get(0);
        return null;
    }

    public static ProxiedPlayer findPlayerInDatabase(String discordID) {
        Map<Object, String> inverseData = SimplifiedDatabase.getInvertedData();
        for (Map.Entry<Object, String> map : inverseData.entrySet()) {
            if (discordID.equals(map.getKey())) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(map.getValue());
                if (p != null)
                    return p;
            }
        }
        return null;
    }

    public static List<Channel> findValidChannels(Class<? extends Channel> tClass, List<String> cID) {
        boolean isText = tClass.isInstance(VoiceChannel.class);
        Bot bot = RocketBot.getInstance().getBot();
        List<Channel> out = new ArrayList<>();
        cID.forEach((s) -> {
            if (!s.isEmpty() && bot.getJda() != null) {
                if (StringUtils.isNumeric(s)) {
                    Channel c = isText ? bot.getJda().getTextChannelById(s) : bot.getJda().getVoiceChannelById(s);
                    if (c != null)
                        out.add(c);
                } else {
                    List<Guild> guilds = bot.getJda().getGuilds();
                    guilds.forEach(g -> (isText ? g.getTextChannels() : g.getVoiceChannels()).forEach((c) -> {
                        if (c.getName().equals(s))
                            out.add(c);
                    }));
                }
            }
        });
        return out;
    }

    public static User findUserInDatabase(ProxiedPlayer p) {
        Map<String, Object> config = SimplifiedDatabase.getData().toMap();
        for (Map.Entry<String, Object> map : config.entrySet()) {
            if (p != null && map.getValue().equals(p.getName())) {
                String userID = SimplifiedDatabase.get(p.getName());
                if (userID != null && !userID.equals("Not Synced yet")) {
                    return RocketBot.getInstance().getBot().getJda().getUserById(userID);
                }
            }
        }
        return null;
    }
}
