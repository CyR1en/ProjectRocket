package com.rocket.rocketbot.configuration;

import com.rocket.rocketbot.RocketBot;

import java.util.List;

public class SConfig extends SFile {

    public SConfig(RocketBot rocketBot) {
        super(rocketBot, "RocketBotConfig");
    }

    public String getBotToken() {
        return getConfig().getString("Bot_Token");
    }

    public String getBotID() {
        return getConfig().getString("Bot_ID");
    }

    public String getOwnerID() {
        return getConfig().getString("Owner_ID");
    }

    public String getCommandTrigger() {
        return getConfig().getString("Command_Trigger");
    }

    public String getDefaultGame() {
        return getConfig().getString("Default_Game");
    }

    public String getPluginLogo() {
        return getConfig().getString("Plugin_Logo");
    }

    public boolean isAutoDelete() {
        return getConfig().getBoolean("Delete_Response");
    }

    public List<String> getTextChannels() {
        return getConfig().getStringList("TextChannels");
    }

    public List<String> getVoiceChannels() {
        return getConfig().getStringList("VoiceChannels");
    }

    public List<String> getBanChannels() {
        return getConfig().getStringList("BanChannels");
    }

    public List<String> getRewardChannels() {
        return getConfig().getStringList("RewardChannels");
    }

    public long getPeriod() {
        return getConfig().getLong("Period");
    }

    public int getMinCount() {
        return getConfig().getInt("MinCount");
    }

}
