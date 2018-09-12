package com.rocket.rocketclient.listeners;

import com.rocket.rocketclient.RocketClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserJoin implements Listener {

    private RocketClient rocketClient;

    public UserJoin(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
    }

    @EventHandler
    public void onUserJoin(PlayerJoinEvent e) {
        rocketClient.getServer().getScheduler().runTaskLaterAsynchronously(rocketClient, () -> {
            System.out.println("loaded data for " + e.getPlayer().getName());
            String g = rocketClient.getGroup(e.getPlayer().getName());
            Player target = Bukkit.getServer().getPlayer(e.getPlayer().getName());
            rocketClient.sendToBungeeCord(target, "RJGQuery", g, e.getPlayer().getName());
            System.out.println("Sent RJGQuery");
        }, 80L);
    }
}
