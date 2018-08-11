package com.rocket.rocketbot.accountSync;

import com.rocket.rocketbot.entity.UnifiedUser;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class AccountDataFormat {

    private String discordID;
    private String discordUsername;
    private String mcGroupName;
    private boolean rewarded;

    public AccountDataFormat(UnifiedUser mcUser) {
        discordID = mcUser.getDUser() == null ? "Not Synced yet" : mcUser.getDUser().getID();
        discordUsername = mcUser.getDUser() == null ? "Not Synced yet" : mcUser.getDUser().getName();
        mcGroupName = mcUser.getDUser() == null ? "Not Synced yet" :
                Database.getJSONObject(mcUser.getProxiedPlayer().getName()).getString(DataKey.MC_GROUP.toString());
        rewarded = mcUser.getDUser() != null && (Database.getJSONObject(mcUser.getProxiedPlayer().getName()).getBoolean(DataKey.REWARDED.toString()));
    }

    public AccountDataFormat(JSONObject accData) {
        discordID = accData.getString(DataKey.DISCORD_ID.toString());
        discordUsername = accData.getString(DataKey.DISCORD_USERNAME.toString());
        mcGroupName = accData.getString(DataKey.MC_GROUP.toString());
        rewarded = accData.getBoolean(DataKey.REWARDED.toString());
    }

    public LinkedHashMap<String, Object> dataAsMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(DataKey.MC_GROUP.toString(), mcGroupName);
        map.put(DataKey.DISCORD_ID.toString(), discordID);
        map.put(DataKey.DISCORD_USERNAME.toString(), discordUsername);
        map.put(DataKey.REWARDED.toString(), rewarded);
        return map;
    }
}
