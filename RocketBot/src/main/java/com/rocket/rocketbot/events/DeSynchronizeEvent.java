package com.rocket.rocketbot.events;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class DeSynchronizeEvent extends Event {

    @Getter
    private final ProxiedPlayer proxiedPlayer;

    public DeSynchronizeEvent(ProxiedPlayer proxiedPlayer) {
        this.proxiedPlayer = proxiedPlayer;
    }

}
