package com.rocket.rocketclient;

import com.cyr1en.mcutils.config.ConfigManager;
import com.cyr1en.mcutils.logger.Logger;
import com.cyr1en.mcutils.utils.reflection.Initializable;
import com.cyr1en.mcutils.utils.reflection.annotation.Initialize;
import com.cyr1en.mcutils.utils.reflection.annotation.process.Initializer;
import com.rocket.rocketclient.configuration.RConfigManager;
import com.rocket.rocketclient.configuration.files.ClientConfig;
import com.rocket.rocketclient.listeners.PluginChannelListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

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
        getServer().getMessenger().registerIncomingPluginChannel(this, "Return", new PluginChannelListener(this));
        Logger.info(" - Now listening for Plugin Messages from BungeeCord");
    }

    public RocketClient getInstance() {
        if (instance == null)
            instance = new RocketClient();
        return instance;
    }

}
