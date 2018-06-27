package com.rocket.rocketbot.commands.discordCommands;


import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;
import com.rocket.rocketbot.entity.Messenger;

import java.time.OffsetDateTime;

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
        String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.reloaded").finish();
        e.reply(Messenger.embedMessage(e.getJDA(), msg, Messenger.ResponseLevel.INFO, OffsetDateTime.now(), e.getGuild().getSelfMember().getColor()), autoDeleteConsumer(e));
    }
}
