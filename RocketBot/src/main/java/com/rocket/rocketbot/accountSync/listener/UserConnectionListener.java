package com.rocket.rocketbot.accountSync.listener;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.entity.UnifiedUser;
import com.rocket.rocketbot.listeners.SListener;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;


public class UserConnectionListener extends SListener {

    public UserConnectionListener(RocketBot rocketBot) {
        super(rocketBot);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        JSONObject data = Database.get(event.getPlayer().getName());
        UnifiedUser unifiedUser = new UnifiedUser(event.getPlayer());
        if (data == null) {
            Database.set(unifiedUser.getProxiedPlayer().getName(), new JSONObject(unifiedUser.getDataAsMap()));
        } else {
            if (!checkDataNodes(unifiedUser, data))
                getRocketBot().getLogger().log(Level.WARNING, "There was an issue with " + event.getPlayer().getName() + "'s account sync data and have been re-initiated.");
        }
    }

    private boolean checkDataNodes(UnifiedUser unifiedUser, JSONObject data) {
        for (DataKey dataKey : DataKey.values())
            try {
                data.get(dataKey.toString());
            } catch (JSONException ex) {
                Database.set(unifiedUser.getProxiedPlayer().getName(), new JSONObject(unifiedUser.getDataAsMap()));
                return false;
            }
        return true;
    }
}
