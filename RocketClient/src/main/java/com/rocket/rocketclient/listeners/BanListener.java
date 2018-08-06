package com.rocket.rocketclient.listeners;

import com.rocket.rocketclient.RocketClient;
import litebans.api.Events;

public class BanListener extends Events.Listener {
    private final RocketClient client;

    public BanListener(RocketClient rocketClient) {
        client = rocketClient;
    }

    @Override
    public void broadcastSent(String message, String type) {
        if(type != null && type.equals("broadcast"))
            client.sendToBungeeCord(client.getServer(), "LBBroadcast", message);
    }
}
