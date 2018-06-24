package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.events.SynchronizeEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SynchronizeListener extends SListener {

    public SynchronizeListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void onSync(SynchronizeEvent event) {
        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bb);
        try {
            out.writeUTF("Forward");
            out.writeUTF(event.getProxiedPlayer().getServer().getInfo().getName());
            out.writeUTF("r-sync");
            out.writeUTF(event.getProxiedPlayer().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.getProxiedPlayer().getServer().sendData("GlobalSystem", bb.toByteArray());
    }
}
