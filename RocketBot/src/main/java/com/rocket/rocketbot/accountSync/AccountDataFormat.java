package com.rocket.rocketbot.accountSync;

import com.rocket.rocketbot.entity.UnifiedUser;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class AccountDataFormat {

    private String discordID;
    private String mcUsername;
    private String discordUsername;

    public AccountDataFormat(UnifiedUser mcUser) {
        discordID = mcUser.getDUser() == null ? "Not Synced yet" : mcUser.getDUser().getID();
        mcUsername = ChatColor.stripColor(mcUser.getProxiedPlayer().getName());
        discordUsername = mcUser.getDUser() == null ? "Not Synced yet" : mcUser.getDUser().getName() ;
    }

    public AccountDataFormat(String keyUUID, JSONObject accData) {
        discordID = accData.getString(DataKey.DISCORD_ID.toString());
        mcUsername = accData.getString(DataKey.MC_USERNAME.toString());
        discordUsername = accData.getString(DataKey.DISCORD_USERNAME.toString());
    }

    public LinkedHashMap<String, Object> dataAsMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(DataKey.MC_USERNAME.toString(), mcUsername);
        map.put(DataKey.DISCORD_ID.toString(), discordID);
        map.put(DataKey.DISCORD_USERNAME.toString(), discordUsername);
        return map;
    }
}
