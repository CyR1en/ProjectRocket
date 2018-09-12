package com.rocket.rocketbot.accountSync.listener;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.listeners.SListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserConnectionListener extends SListener {

    public UserConnectionListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(getRocketBot(), () -> {
            try{
                ResultSet row = getRocketBot().getDb().getRowByName(event.getPlayer().getName());
                if (!row.next())
                    getRocketBot().getDb().insertNew(event.getPlayer().getName());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
