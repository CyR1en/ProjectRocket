package com.rocket.rocketbot.commands.discordCommands;


import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;

public class ReloadCmd extends DCommand {

    public ReloadCmd(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "reload";
        this.help = "Reloads RocketBot configuration.";
        this.category = Bot.Categories.MISC.getCategory();
        this.type = Type.EMBED;
    }

    @Override
    protected void doCommand(CommandEvent e) {
        rocketBot.reload();
        String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.reload").finish();
        respond(ResponseLevel.LEVEL_1, e, msg);
    }
}
