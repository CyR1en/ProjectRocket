package com.rocket.rocketclient.listeners;

import com.rocket.rocketclient.RocketClient;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class UserConnect implements Listener {
    private final RocketClient client;

    public UserConnect(RocketClient rocketClient) {
        client = rocketClient;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(client, () -> {
            String g = client.getGroup(event.getPlayer().getName());
            client.sendToBungeeCord(event.getPlayer(), "RJGQuery", g, event.getPlayer().getUniqueId().toString());
        }, 40L);
    }
}
