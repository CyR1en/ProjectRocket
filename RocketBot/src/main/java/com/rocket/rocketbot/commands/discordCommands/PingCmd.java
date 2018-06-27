package com.rocket.rocketbot.commands.discordCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class PingCmd extends DCommand {

    private EventWaiter waiter;

    public PingCmd(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "ping";
        this.aliases = new String[]{"pong, p"};
        this.help = "Check bot's connectivity";
        this.category = Bot.Categories.MISC.getCategory();
        waiter = getRocketBot().getEventWaiter();
    }

    @Override
    protected void doCommand(CommandEvent e) {
        e.getTextChannel().sendMessage("ping...").queue(msg -> msg.editMessage("ping: `" + e.getMessage().getCreationTime()
                .until(msg.getCreationTime(), ChronoUnit.MILLIS) + " ms`").queue((m) -> {
            if (auto)
                scheduler.schedule(() -> m.delete().queue(), RESPONSE_DURATION, TimeUnit.MINUTES);
        }));
    }
}
