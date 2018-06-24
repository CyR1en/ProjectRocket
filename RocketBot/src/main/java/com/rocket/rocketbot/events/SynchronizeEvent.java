package com.rocket.rocketbot.events;

import com.rocket.rocketbot.entity.UnifiedUser;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class SynchronizeEvent extends Event {

    @Getter private final ProxiedPlayer proxiedPlayer;
    @Getter private final UnifiedUser unifiedUser;

    public SynchronizeEvent(ProxiedPlayer proxiedPlayer, UnifiedUser unifiedUser) {
        this.proxiedPlayer = proxiedPlayer;
        this.unifiedUser = unifiedUser;
    }
}
