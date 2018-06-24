package com.rocket.rocketbot.utils;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FinderUtils {

    public static List<Member> findMember(String query, Guild guild) {
        String id;
        if (query.matches("<#\\d+>")) {
            id = query.replaceAll("<#(\\d+)", "$1");
            Member member = guild.getMemberById(id);
            if (member != null && member.getGuild().equals(guild))
                return Collections.singletonList(member);
        } else if (StringUtils.isNumeric(query)) {
            Member member = guild.getMemberById(query);
            if (member != null && member.getGuild().equals(guild))
                return Collections.singletonList(member);
        }
        ArrayList<Member> exact = new ArrayList<>();
        ArrayList<Member> wrongcase = new ArrayList<>();
        ArrayList<Member> startswith = new ArrayList<>();
        ArrayList<Member> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        guild.getMembers().forEach(m -> {
            if (m.getEffectiveName().equals(lowerQuery))
                exact.add(m);
            else if (m.getEffectiveName().equalsIgnoreCase(lowerQuery) && exact.isEmpty())
                wrongcase.add(m);
            else if (m.getEffectiveName().toLowerCase().startsWith(lowerQuery) && wrongcase.isEmpty())
                startswith.add(m);
            else if (m.getEffectiveName().toLowerCase().contains(lowerQuery) && startswith.isEmpty())
                contains.add(m);
        });
        if (!exact.isEmpty())
            return exact;
        if (!wrongcase.isEmpty())
            return wrongcase;
        if (!startswith.isEmpty())
            return startswith;
        return contains;
    }

    public static Member findMember(String query) {
        List<Guild> guilds = RocketBot.getInstance().getBot().getJda().getGuilds();
        List<Member> members;
        for (Guild guild : guilds) {
            members = findMember(query, guild);
            if (members.size() > 0)
                return members.get(0);
        }
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
