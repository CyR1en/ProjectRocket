package com.rocket.rocketbot.accountSync;

import lombok.Getter;

public enum DataKey {
    MC_GROUP("Minecraft-group"),
    DISCORD_ID("Discord-ID"),
    DISCORD_USERNAME("Discord-Username"),
    REWARDED("Rewarded");

    @Getter private String key;

    DataKey(String s) {
        this.key = s;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
    