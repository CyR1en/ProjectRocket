package com.rocket.rocketbot.utils;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FinderUtils {

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
            if(discordID.equals(map.getKey())) {
                UUID uuid = UUID.fromString(map.getValue());
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if(p != null)
                    return  p;
            }
        }
        return null;
    }

    public static User findUserInDatabase(ProxiedPlayer p) {
        Map<String, Object> config = SimplifiedDatabase.getData().toMap();
        for (Map.Entry<String, Object> map : config.entrySet()) {
            if (p != null && map.getValue().equals(p.getUniqueId())) {
                String userID = SimplifiedDatabase.get(p.getUniqueId().toString());
                if (userID != null && !userID.equals("Not Synced yet")) {
                    return RocketBot.getInstance().getBot().getJda().getUserById(userID);
                }
            }
        }
        return null;
    }
}
