package com.rocket.rocketbot.commands.discordCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;

import java.util.List;
import java.util.stream.Collectors;

public class HelpCmd extends DCommand {

    public HelpCmd(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "help";
        this.help = "List all commands and show usage for a command.";
        this.category = Bot.Categories.HELP.getCategory();
    }

    @Override
    protected void doCommand(CommandEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = getRocketBot().getBot().getJda();
        eb.setColor(e.getGuild().getMember(jda.getSelfUser()).getColor());
        eb.setDescription("For more detailed help, do " + getRocketBot().getBot().getClient().getPrefix() + "<command> help.");
        if (e.getArgs().isEmpty()) {
            eb.setAuthor("RocketBot Commands", null, getRocketBot().getConfig().getPluginLogo());
            eb = listCommands(eb);
            eb.setFooter("RocketBot", null);
            respond(e, eb.build());
        }
    }

    private EmbedBuilder listCommands(EmbedBuilder ebi) {
        Bot.Categories.valuesAsList().forEach(c -> {
            StringBuilder str = new StringBuilder();
            if (getAllCommandsWithCategoryOf(c.getCategory()).size() != 0) {
                for (Command cmd : getAllCommandsWithCategoryOf(c.getCategory())) {
                    str.append(getRocketBot().getBot().getClient().getPrefix()).append(cmd.getName())
                            .append(cmd.getArguments() == null ? "" : " " + cmd.getArguments())
                            .append(" - ").append(cmd.getHelp()).append("\n");
                }
                ebi.addField(" - " + c.getCategory().getName(), str.toString(), false);
            }
        });
        return ebi;
    }

    private List<Command> getAllCommandsWithCategoryOf(Command.Category category) {
        List<Command> registered = getRocketBot().getBot().getClient().getCommands();
        return registered.stream().filter(c -> c.getCategory().equals(category))
                .sorted().collect(Collectors.toList());
    }

}