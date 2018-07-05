package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotReady extends ListenerAdapter {

    private RocketBot rocketBot;

    public BotReady(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
        rocketBot.getLogger().info("- Bot is now ready");
    }

    @Override
    public void onReady(ReadyEvent event) {
        rocketBot.getBroadcaster().loadChannels();
    }
}
