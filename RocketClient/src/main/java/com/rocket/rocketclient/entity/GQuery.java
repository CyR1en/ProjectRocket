package com.rocket.rocketclient.entity;

import com.cyr1en.mcutils.logger.Logger;
import com.rocket.rocketclient.RocketClient;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class GQuery implements PCM {

    private RocketClient rocketClient;

    @Getter
    private String name;

    public GQuery(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
        name = "GQuery";
    }

    @Override
    public void handle(String s, Player player, byte[] bytes) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            in.readUTF(); //skip the first data
            String sUUID = in.readUTF();
            String group = rocketClient.getGroup(sUUID);
            rocketClient.sendToBungeeCord(player, "RQuery", group != null ? group : "null", player.getUniqueId().toString());
            Logger.info(String.format("[%s] Pong-1!", this.getClass().getSimpleName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
