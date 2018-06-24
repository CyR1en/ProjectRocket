package com.rocket.rocketbot.entity;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;


public class Messenger {

    public static MessageEmbed embedMessage(CommandEvent event, MessageEmbed message, ResponseLevel level) {
        EmbedBuilder embedBuilder = new EmbedBuilder(message);
        User bot = event.getJDA().getSelfUser();
        embedBuilder.setAuthor(bot.getName() + " #" + bot.getDiscriminator(),
                null, bot.getEffectiveAvatarUrl());
        embedBuilder.setFooter(level.getFooter(), null);
        embedBuilder.setTimestamp(event.getMessage().getCreationTime());
        embedBuilder.setColor(ResponseLevel.WARNING.getColor());
        return embedBuilder.build();
    }

    public static MessageEmbed embedMessage(CommandEvent event, String message, ResponseLevel level) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(message);
        User bot = event.getJDA().getSelfUser();
        embedBuilder.setAuthor(bot.getName() + " #" + bot.getDiscriminator(),
                null, bot.getEffectiveAvatarUrl());
        embedBuilder.setFooter(level.getFooter(), null);
        embedBuilder.setTimestamp(event.getMessage().getCreationTime());
        embedBuilder.setColor(ResponseLevel.WARNING.getColor());
        return embedBuilder.build();
    }

    public enum ResponseLevel {
        SUCCESS(new Color(92, 184, 92), RocketBot.getLocale().getTranslatedMessage("responselevel.lvl1").finish()),
        WARNING(new Color(243, 119, 54), RocketBot.getLocale().getTranslatedMessage("responselevel.lvl2").finish()),
        ERROR(new Color(188, 75, 79), RocketBot.getLocale().getTranslatedMessage("responselevel.lvl3").finish());

        private Color color;
        private String footer;

        ResponseLevel(Color color, String footer) {
            this.color = color;
            this.footer = footer;
        }

        public Color getColor() {
            return color;
        }

        public String getFooter() {
            return footer;
        }
    }
}