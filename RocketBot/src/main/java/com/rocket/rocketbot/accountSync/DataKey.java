package com.rocket.rocketbot.accountSync;

public enum DataKey {
    MC_USERNAME("Minecraft-Username"),
    MC_GROUP("Minecraft-group"),
    DISCORD_ID("Discord-ID"),
    DISCORD_USERNAME("Discord-Username");

    private String s;

    DataKey(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
    