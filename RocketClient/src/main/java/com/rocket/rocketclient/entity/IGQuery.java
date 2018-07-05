package com.rocket.rocketclient.entity;

import com.rocket.rocketclient.RocketClient;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class IGQuery implements PCM {

    private RocketClient rocketClient;

    @Getter
    private String name;

    public IGQuery(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
        name = "IGQuery";
    }

    @Override
    public void handle(String s, Player player, byte[] bytes) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            String sUUID = in.readUTF();
            String group = rocketClient.getGroup(sUUID);
            rocketClient.sendToBungeeCord(player, "RIGQuery", group != null ? group : "null", player.getUniqueId().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
