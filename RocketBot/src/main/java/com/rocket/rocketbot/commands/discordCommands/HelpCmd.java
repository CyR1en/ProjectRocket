package com.rocket.rocketbot.commands.discordCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Collections;

public class HelpCmd extends DCommand {

    public HelpCmd(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "help";
        this.help = "List all commands and show usage for a command.";
        //this.category = Bot.HELP;
    }

    @Override
    protected void doCommand(CommandEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = getRocketBot().getBot().getJda();
        eb.setColor(e.getGuild().getMember(jda.getSelfUser()).getColor());
        eb.setDescription("For more detailed help, do " + getRocketBot().getBot().getClient().getPrefix() + "<command> help.");
        if (e.getArgs().isEmpty()) {
            eb.setAuthor("SeasonsTime Commands", null, getRocketBot().getConfig().getPluginLogo());
            User user = jda.getUserById("193970511615623168");
            eb = listCommands(eb);
            if (user != null) {
                eb.setFooter("For more help, contact " + user.getName() + "#" + user.getDiscriminator(), user.getAvatarUrl());
                respond(e, eb.build());
            }
        }
    }

    private EmbedBuilder listCommands(EmbedBuilder ebi) {
        Command.Category[] categories = {Bot.MISC, Bot.HELP, Bot.INFO};
        for (int i = 0; i <= categories.length - 1; i++) {
            StringBuilder str = new StringBuilder();
            if (getAllCommandsWithCategoryOf(categories[i]).size() != 0) {
                for (Command c : getAllCommandsWithCategoryOf(categories[i])) {
                    str.append(getSt().getBot().getClient().getPrefix()).append(c.getName())
                            .append(c.getArguments() == null ? "" : " " + c.getArguments())
                            .append(" - ").append(c.getHelp()).append("\n");
                }
                ebi.addField(" - " + categories[i].getName(), str.toString(), false);
            }
        }
        return ebi;
    }

    private java.util.List<DCommand> getAllCommandsWithCategoryOf(Command.Category category) {
        ArrayList<DCommand> commands = new ArrayList<>();
        for (Command c : getSt().getBot().getClient().getCommands()) {
            if (c.getCategory().equals(category))
                commands.add((DCommand) c);
        }
        Collections.sort(commands);
        return commands;
    }

}