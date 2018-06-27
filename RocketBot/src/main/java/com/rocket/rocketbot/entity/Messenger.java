package com.rocket.rocketbot.entity;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.time.OffsetDateTime;


public class Messenger {

    public static MessageEmbed embedMessage(CommandEvent event, MessageEmbed message, ResponseLevel level) {
        EmbedBuilder embedBuilder = new EmbedBuilder(message);
        User bot = event.getJDA().getSelfUser();
        embedBuilder.setAuthor(bot.getName() + " #" + bot.getDiscriminator(),
                null, bot.getEffectiveAvatarUrl());
        embedBuilder.setFooter(level.getFooter(), null);
        embedBuilder.setTimestamp(event.getMessage().getCreationTime());
        embedBuilder.setColor(level.getColor());
        return embedBuilder.build();
    }

    public static MessageEmbed embedMessage(CommandEvent event, String message, ResponseLevel level) {
        return embedMessage(event.getJDA(), message, level, event.getMessage().getCreationTime(), null);
    }

    public static MessageEmbed embedMessage(JDA jda, String message, ResponseLevel level, OffsetDateTime odt, Color color) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(message);
        User bot = jda.getSelfUser();
        embedBuilder.setAuthor(bot.getName() + " #" + bot.getDiscriminator(),
                null, bot.getEffectiveAvatarUrl());
        embedBuilder.setFooter(level.getFooter(), null);
        embedBuilder.setTimestamp(odt);
        Color c = color == null ? level.getColor() : color;
        embedBuilder.setColor(c);
        return embedBuilder.build();
    }

    public enum ResponseLevel {
        SUCCESS(new Color(92, 184, 92), RocketBot.getLocale().getTranslatedMessage("responselevel.lvl1").finish()),
        WARNING(new Color(243, 119, 54), RocketBot.getLocale().getTranslatedMessage("responselevel.lvl2").finish()),
        ERROR(new Color(188, 75, 79), RocketBot.getLocale().getTranslatedMessage("responselevel.lvl3").finish()),
        INFO(RocketBot.getLocale().getTranslatedMessage("responselevel.information").finish()),
        BROADCAST(RocketBot.getLocale().getTranslatedMessage("responselevel.broadcast").finish());

        @Getter private Color color;
        @Getter private String footer;

        ResponseLevel(Color color, String footer) {
            this.color = color;
            this.footer = footer;
        }

        ResponseLevel(String footer) {
            this.color = Color.white;
            this.footer = footer;
        }
    }
}