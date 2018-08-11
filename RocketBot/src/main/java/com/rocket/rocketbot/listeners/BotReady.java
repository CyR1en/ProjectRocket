package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.tasks.RewardTask;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

public class BotReady extends ListenerAdapter {

    private RocketBot rocketBot;

    public BotReady(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        rocketBot.getLogger().info("- Bot is now ready");
        rocketBot.getBroadcaster().loadChannels();
        rocketBot.getBot().getJda().addEventListener(new DUserListen(rocketBot));
        ProxyServer.getInstance().getPluginManager().registerListener(rocketBot, new LBBroadcast(rocketBot));
        registerTasks();
    }

    private void registerTasks() {
        long qP = rocketBot.getConfig().getPeriod();
        rocketBot.getScheduler().scheduleAtFixedRate(new RewardTask(rocketBot), qP, qP, TimeUnit.SECONDS);
    }
}
