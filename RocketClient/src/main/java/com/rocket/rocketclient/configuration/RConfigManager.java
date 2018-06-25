package com.rocket.rocketclient.configuration;

import com.cyr1en.mcutils.config.ConfigManager;
import com.rocket.rocketclient.configuration.annotation.Configuration;
import com.rocket.rocketclient.configuration.enums.Config;
import com.rocket.rocketclient.configuration.files.ClientConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RConfigManager {

    private ConfigManager manager;
    private HashMap<Config, BaseConfig> configs;

    public RConfigManager(ConfigManager manager) {
        this.manager = manager;
        this.configs = new HashMap<>();
    }

    @SafeVarargs
    public final boolean setupConfigs(Class<? extends BaseConfig>... classes) {
        boolean isSafeToStart = true;
        for (Class<? extends BaseConfig> config : classes) {
            if (config.isAnnotationPresent(Configuration.class)) {
                Configuration meta = config.getAnnotation(Configuration.class);
                try {
                    BaseConfig baseConfig = config.getConstructor(ConfigManager.class, String[].class).newInstance(manager, meta.header());
                    if(!baseConfig.init())
                        isSafeToStart = false;
                    configs.put(meta.type(), baseConfig);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSafeToStart;
    }

    public BaseConfig getConfig(Config config) {
        return configs.get(config);
    }

    public ClientConfig getGeneralConfig() {
        return (ClientConfig) getConfig(Config.GENERAL);
    }

}
