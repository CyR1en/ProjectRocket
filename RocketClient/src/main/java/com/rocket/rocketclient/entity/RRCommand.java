package com.rocket.rocketclient.entity;

import com.rocket.rocketclient.RocketClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RRCommand implements PCM {

    private RocketClient rocketClient;

    @Getter private String name;

    public RRCommand(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
        name = "RRCommand";
    }

    @Override
    public void handle(String s, Player player, byte[] bytes) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            in.readUTF(); //skip the first data
            String name = in.readUTF();
            String command = rocketClient.getRConfigManager().getGeneralConfig().getRCommand();
            command = command.replace("{player}", name);
            command = command.replaceAll("/", "").trim();
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
