package com.rocket.rocketbot.configuration;

import com.google.common.io.ByteStreams;
import com.rocket.rocketbot.RocketBot;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class SFile {

    @Getter private String name;
    @Getter private Configuration config;
    @Getter private RocketBot rocketBot;

    /**
     * Construct that will initialize the Configuration.
     *
     * @param rocketBot SeasonsTime instance.
     */
    public SFile(RocketBot rocketBot, String name) {
        this.name = name;
        this.rocketBot = rocketBot;
        initConfig();
    }

    /**
     * Reload the file
     */
    public void sReload() {
        save();
        initConfig();
    }

    public void reload() {
        initConfig();
    }

    /**
     * save the changes that have been done to the config
     */
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(rocketBot.getDataFolder(), name + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Initialize config and generate new config if the config does not exist in the data folder.
     */
    private void initConfig() {
        if (!rocketBot.getDataFolder().exists()) {
            rocketBot.getDataFolder().mkdir();
        }

        File file = new File(rocketBot.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (InputStream in = rocketBot.getResourceAsStream("default_" + name + ".yml");
                 OutputStream out = new FileOutputStream(file)) {
                ByteStreams.copy(in, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
