package com.rocket.rocketbot.commands.discordCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;

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
            eb.setAuthor("RocketBot Commands", null, getRocketBot().getConfig().getPluginLogo());
            eb.setFooter("RocketBot", null);
            respond(e, eb.build());

        }
    }

    private EmbedBuilder listCommands(EmbedBuilder ebi) {
        Bot.Categories[] categories = Bot.Categories.values();
        for (int i = 0; i <= categories.length - 1; i++) {
            StringBuilder str = new StringBuilder();
            if (getAllCommandsWithCategoryOf(categories[i].getCategory()).size() != 0) {
                for (Command c : getAllCommandsWithCategoryOf(categories[i].getCategory())) {
                    str.append(getRocketBot().getBot().getClient().getPrefix()).append(c.getName())
                            .append(c.getArguments() == null ? "" : " " + c.getArguments())
                            .append(" - ").append(c.getHelp()).append("\n");
                }
                ebi.addField(" - " + categories[i].getCategory().getName(), str.toString(), false);
            }
        }
        return ebi;
    }

    private java.util.List<DCommand> getAllCommandsWithCategoryOf(Command.Category category) {
        ArrayList<DCommand> commands = new ArrayList<>();
        for (Command c : getRocketBot().getBot().getClient().getCommands()) {
            if (c.getCategory().equals(category))
                commands.add((DCommand) c);
        }
        Collections.sort(commands);
        return commands;
    }

}