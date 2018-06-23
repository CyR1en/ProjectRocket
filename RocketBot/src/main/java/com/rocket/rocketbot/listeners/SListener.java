package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Listener;

public class SListener implements Listener {

    @Getter private RocketBot rocketBot;

    public SListener(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
    }

}

