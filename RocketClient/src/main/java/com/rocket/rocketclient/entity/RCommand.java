package com.rocket.rocketclient.entity;

import com.cyr1en.mcutils.logger.Logger;
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
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        Logger.info(String.format("[%s] Pong-2!", this.getClass().getSimpleName()));
    }

}
