package com.rocket.rocketbot.commands.discordCommands;


import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;

public class ReloadCmd extends DCommand {

    public ReloadCmd(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "reload";
        this.help = "Reloads SeasonsTime's configuration.";
        //this.category = Bot.MISC;
        this.type = Type.EMBED;
    }

    @Override
    protected void doCommand(CommandEvent e) {
        getRocketBot().getConfig().reload();
        respond(ResponseLevel.LEVEL_1, e, "Configuration have been reloaded successfully!");
    }
}
