package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.events.DeSynchronizeEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.event.EventHandler;

public class DeSyncListener extends SListener{
    public DeSyncListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void sendMessage(DeSynchronizeEvent event) {
        String msg = RocketBot.getLocale().getTranslatedMessage("sync.desync").finish();
        event.getProxiedPlayer().sendMessage((ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &6" + msg)));
    }
}
