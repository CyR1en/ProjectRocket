package com.rocket.rocketclient.configuration;

import com.cyr1en.mcutils.config.Config;
import com.cyr1en.mcutils.config.ConfigManager;
import com.cyr1en.mcutils.logger.Logger;

import java.io.File;
import java.util.List;

public abstract class BaseConfig implements IMCBConfig {

    private final String[] header;
    private ConfigManager configManager;
    protected Config config;

    protected BaseConfig(ConfigManager configManager, String[] header) {
        this.configManager = configManager;
        this.header = header;
    }

    protected void initNode(Node node) {
        String[] comment = node.getComment();
        if (config.get(node.key()) == null) {
            config.set(node.key(), node.getDefaultValue(), comment);
            config.saveConfig();
        }
    }

    public boolean init() {
        File f = configManager.getConfigFile(this.getClass().getSimpleName() + ".yml");
        if (!f.exists()) {
            Logger.warn(this.getClass().getSimpleName() + ".yml" + " have been generated or new fields have been added. " +
                    "Please make sure to fill in all config fields correctly. Server will be stopped for safety.");
            config = configManager.getNewConfig(this.getClass().getSimpleName() + ".yml", header);
            initialize();
            return false;
        }
        config = configManager.getNewConfig(this.getClass().getSimpleName() + ".yml", header);
        initialize();
        return true;
    }

    @Override
    public String getString(Node node) {
        return config.getString(node.key());
    }

    @Override
    public boolean getBoolean(Node node) {
        return config.getBoolean(node.key());
    }

    @Override
    public int getInt(Node node) {
        return config.getInt(node.key());
    }

    @Override
    public List getList(Node node) {
        return config.getList(node.key());
    }

    public abstract void initialize();

    public Config getConfig() {
        return config;
    }
}
