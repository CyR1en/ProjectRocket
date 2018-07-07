package com.rocket.rocketclient.listeners;

import com.cyr1en.mcutils.logger.Logger;
import com.rocket.rocketclient.RocketClient;
import com.rocket.rocketclient.entity.PCM;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PluginChannelListener implements PluginMessageListener {

    private RocketClient rocketClient;
    private List<PCM> pcms;

    public PluginChannelListener(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
        pcms = new ArrayList<>();
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            String sub = in.readUTF();
            pcms.forEach(pcm -> {
                if (sub.equals(pcm.getName()))
                    pcm.handle(s, player, bytes);
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addPCM(Class<? extends PCM> pcm) {
        try {
            PCM iPcm = pcm.getConstructor(RocketClient.class).newInstance(rocketClient);
            pcms.add(iPcm);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
