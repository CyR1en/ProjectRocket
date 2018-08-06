package com.rocket.rocketbot.entity;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.utils.Finder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class DUser {

    @Getter private String name;
    @Getter private HashMap<Guild, String> nickNames;
    @Getter private String ID;
    @Getter private User user;

    @Getter @Setter private UUID mcUUID;

    public DUser(MessageReceivedEvent e) {
        nickNames = new HashMap<>();
        name = e.getAuthor().getName();
        nickNames.put(e.getGuild(), e.getMember().getNickname());
        ID = e.getAuthor().getId();
        mcUUID = null;
        user = e.getAuthor();
    }

    public DUser(CommandEvent e) {
        this(e.getEvent());
    }

    public DUser(User user, Guild guild) {
        nickNames = new HashMap<>();
        name = user.getName();
        nickNames.put(guild, guild.getMember(user).getNickname());
        ID = user.getId();
    }

    public DUser(User user) {
        nickNames = new HashMap<>();
        name = user.getName();
        ID = user.getId();
        RocketBot.getInstance().getBot().getJda().getMutualGuilds(user).forEach(guild -> nickNames.put(guild, guild.getMember(user).getNickname()));
    }

    public ProxiedPlayer parseAsPlayer() {
        return Finder.findPlayerInDatabase(this.ID);
    }

    @Override
    public String toString() {
        return getName() + "|" + getNickNames().toString() + "|" + getID();
    }
}
