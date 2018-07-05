package com.rocket.rocketclient.configuration.files;

import com.cyr1en.mcutils.config.ConfigManager;
import com.rocket.rocketclient.configuration.BaseConfig;
import com.rocket.rocketclient.configuration.Node;
import com.rocket.rocketclient.configuration.annotation.Configuration;
import com.rocket.rocketclient.configuration.enums.Config;

@Configuration(type = Config.GENERAL, header = {"RocketClient Configuration"})
public class ClientConfig extends BaseConfig {

    public ClientConfig(ConfigManager configManager, String[] header) {
        super(configManager, header);
    }

    @Override
    public void initialize() {
        for (CNode node : CNode.values())
            initNode(node);
    }

    public String getCommand() {
        return getString(CNode.COMMAND);
    }

    enum CNode implements Node {
        COMMAND("Command", new String[]{"command to be dispatched", "when a player syncs their", "accounts"}, "say cheese!");

        private String key;
        private String[] comment;
        private Object defaultValue;

        CNode(String key, String[] comment, Object defaultValue) {
            this.key = key;
            this.comment = comment;
            this.defaultValue = defaultValue;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String[] getComment() {
            return comment;
        }

        @Override
        public String key() {
            return key;
        }
    }
}
