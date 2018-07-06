package com.rocket.rocketclient;

import com.cyr1en.mcutils.config.ConfigManager;
import com.cyr1en.mcutils.logger.Logger;
import com.cyr1en.mcutils.utils.reflection.Initializable;
import com.cyr1en.mcutils.utils.reflection.annotation.Initialize;
import com.cyr1en.mcutils.utils.reflection.annotation.process.Initializer;
import com.rocket.rocketclient.configuration.RConfigManager;
import com.rocket.rocketclient.configuration.files.ClientConfig;
import com.rocket.rocketclient.entity.GQuery;
import com.rocket.rocketclient.entity.IGQuery;
import com.rocket.rocketclient.entity.RCommand;
import com.rocket.rocketclient.listeners.PluginChannelListener;
import lombok.Getter;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.nodetype.types.DisplayNameType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class RocketClient extends JavaPlugin implements Initializable {

    private static RocketClient instance;

    @Getter
    private RConfigManager rConfigManager;

    @Override
    public void onEnable() {
        Logger.init(this.getDescription().getName());
        Initializer.initAll(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Initialize(priority = 0)
    public void initConfig() {
        ConfigManager configManager = new ConfigManager(this);
        rConfigManager = new RConfigManager(configManager);
        if (!rConfigManager.setupConfigs(ClientConfig.class))
            getServer().shutdown();
        Logger.info(" - Loaded Configuration");
    }

    @Initialize(priority = 1)
    public void initPLC() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        PluginChannelListener pcl = new PluginChannelListener(this);
        pcl.addPCM(GQuery.class);
        pcl.addPCM(RCommand.class);
        pcl.addPCM(IGQuery.class);
        getServer().getMessenger().registerIncomingPluginChannel(this, "Return", pcl);
        Logger.info(" - Now listening for Plugin Messages from BungeeCord");
    }

    public RocketClient getInstance() {
        if (instance == null)
            instance = new RocketClient();
        return instance;
    }

    public void sendToBungeeCord(Player p, String channel, String sub, String... other) {
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

    public String getGroup(String uuid) {
        RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
        if (provider != null) {
            LuckPermsApi api = provider.getProvider();
            System.out.println("sUUD = " + uuid);
            UUID uid = UUID.fromString(uuid);
            Player p = Bukkit.getServer().getPlayer(uid);
            User user = api.getUser(p.getUniqueId());
            if (user != null) {
                Group group = api.getGroupManager().getGroup(user.getPrimaryGroup());
                Optional<String> displayName = Optional.empty();
                try {
                    for (Node n : group == null ? new ArrayList<Node>() : group.getOwnNodes()) {
                        Optional<DisplayNameType> displayN = n.getTypeData(DisplayNameType.KEY);
                        if (displayN.isPresent()) {
                            displayName = Optional.of(displayN.get().getDisplayName());
                        }
                    }
                } catch (Exception ignore) {

                }
                String content = displayName.orElse(group != null ? group.getName() : null);
                if(content == null)
                    return null;
                return content;
            }
        }
        return null;
    }
}
