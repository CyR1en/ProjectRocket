package com.rocket.rocketbot.utils;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
        if(discordID == null) return null;
        String username = SimplifiedDatabase.getInvertedData().get(discordID);
        if(username == null) return null;
        else if(!username.equals("Not Synced yet")) {
            return ProxyServer.getInstance().getPlayer(username);
        } else return null;
    }

    public static List<Channel> findValidChannels(Class<? extends Channel> tClass, List<String> cID) {
        boolean isText = tClass.getSimpleName().equals("TextChannel");
        Bot bot = RocketBot.getInstance().getBot();
        List<Channel> out = new ArrayList<>();
        cID.forEach((s) -> {
            if (!s.isEmpty() && bot.getJda() != null) {
                if (StringUtils.isNumeric(s)) {
                    Channel c = isText ? bot.getJda().getTextChannelById(s) : bot.getJda().getVoiceChannelById(s);
                    if (c != null) {
                        out.add(c);
                        RocketBot.getInstance().getLogger().info("- Loaded " + c);
                    }
                } else {
                    List<Guild> guilds = bot.getJda().getGuilds();
                    guilds.forEach(g -> (isText ? g.getTextChannels() : g.getVoiceChannels()).forEach((c) -> {
                        if (c.getName().equals(s)) {
                            out.add(c);
                            RocketBot.getInstance().getLogger().info("- Loaded " + c);
                        }
                    }));
                }
            }
        });
        return out;
    }

    public static User findUserInDatabase(ProxiedPlayer p) {
        if(p == null) return null;
        String id = SimplifiedDatabase.get(p.getName());
        if(id == null) return null;
        else if(!id.equals("Not Synced yet")) {
            return RocketBot.getInstance().getBot().getJda().getUserById(id);
        } else return null;
    }

}
