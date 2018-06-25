package com.rocket.rocketclient.listeners;

import com.rocket.rocketclient.RocketClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PluginChannelListener implements PluginMessageListener {

    private RocketClient rocketClient;

    public PluginChannelListener(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            String sub = in.readUTF();
            if (sub.equals("command")) {
                String command = rocketClient.getRConfigManager().getGeneralConfig().getCommand();
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
