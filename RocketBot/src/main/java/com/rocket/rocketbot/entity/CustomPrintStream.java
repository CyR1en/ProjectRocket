package com.rocket.rocketbot.entity;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.io.PrintStream;

public class CustomPrintStream extends PrintStream {

    private Message message;
    private CommandEvent e;

    public CustomPrintStream(OutputStream out, CommandEvent e, Message message) {
        super(out);
        this.e = e;
        this.message = message;
    }

    @Override
    public void print(String s) {
        super.print(s);
        if(!StringUtils.isBlank(s)) {
            String desc = String.format("Transferring data from %s", e.getArgs());
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription(desc);
            embedBuilder.addField("Transfer Progress", s, false);
            Messenger.ResponseLevel level = s.contains("100%") ? Messenger.ResponseLevel.SUCCESS : Messenger.ResponseLevel.INFO;
            MessageEmbed embed = Messenger.embedMessage(e, embedBuilder.build(), level);
            RocketBot.getInstance().getLogger().info(s);
            message.editMessage(embed).queue();
        }
    }
}
