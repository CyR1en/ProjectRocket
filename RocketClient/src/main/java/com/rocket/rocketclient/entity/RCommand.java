package com.rocket.rocketclient.entity;

import com.rocket.rocketclient.RocketClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RCommand implements PCM {

    private RocketClient rocketClient;

    @Getter private String name;

    public RCommand(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
        name = "RCommand";
    }

    @Override
    public void handle(String s, Player player, byte[] bytes) {
        String command = rocketClient.getRConfigManager().getGeneralConfig().getCommand();
        command = command.replace("{player}", player.getName());
        command = command.replaceAll("/", "").trim();
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }

}
