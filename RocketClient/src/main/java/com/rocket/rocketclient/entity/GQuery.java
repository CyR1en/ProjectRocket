package com.rocket.rocketclient.entity;

import com.rocket.rocketclient.RocketClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class GQuery implements PCM {

    private RocketClient rocketClient;

    @Getter
    private String name;

    private String rChannel;

    public GQuery(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
        name = "GQuery";
        rChannel = "RQuery";
    }

    @Override
    public void handle(String s, Player player, byte[] bytes) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            in.readUTF(); //skip the first data
            String name = in.readUTF();
            String group = rocketClient.getGroup(name);
            Player target = Bukkit.getServer().getPlayer(name);
            rocketClient.sendToBungeeCord(target, rChannel, group != null ? group : "null", target.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
