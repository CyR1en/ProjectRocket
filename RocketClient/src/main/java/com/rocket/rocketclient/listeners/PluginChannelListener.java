package com.rocket.rocketclient.listeners;

import com.cyr1en.mcutils.logger.Logger;
import com.rocket.rocketclient.RocketClient;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.UUID;

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
                Logger.info(String.format("[%s] Pong-2!", this.getClass().getSimpleName()));
            }
            if(sub.equals("GQuery")) {
                RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
                if (provider != null) {
                    LuckPermsApi api = provider.getProvider();
                    String sUUID = in.readUTF();
                    User user = api.getUser(UUID.fromString(sUUID));
                    if(user != null)
                        sendToBungeeCord(player, "RQuery", user.getPrimaryGroup());
                    else
                        sendToBungeeCord(player, "RQuery", "null");
                    Logger.info(String.format("[%s] Pong-1!", this.getClass().getSimpleName()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendToBungeeCord(Player p, String channel, String sub, String... other){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(channel);
            out.writeUTF(sub);
            for (String o : other) {
                out.writeUTF(o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.sendPluginMessage(RocketClient.getPlugin(RocketClient.class), "BungeeCord", b.toByteArray());
    }

}
