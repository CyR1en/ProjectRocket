package com.rocket.rocketclient.entity;

import org.bukkit.entity.Player;

public interface PCM {

    void handle(String s, Player player, byte[] bytes);

    String getName();
}
