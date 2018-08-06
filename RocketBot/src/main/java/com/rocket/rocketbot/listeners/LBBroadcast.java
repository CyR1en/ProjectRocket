package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class LBBroadcast extends SListener{

    public LBBroadcast(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void handleBroadcast(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("LBBroadcast")) {
                    String message = in.readUTF();
                    getRocketBot().getBroadcaster().sendBanMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
