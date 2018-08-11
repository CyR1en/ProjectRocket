package com.rocket.rocketbot.listeners;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;
import org.json.JSONObject;

import java.util.Iterator;

public class DUserLeave extends ListenerAdapter {

    private RocketBot rocketBot;

    public DUserLeave(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(rocketBot, () -> processAsync(event));
    }

    private void processAsync(GuildMemberLeaveEvent event) {
        User user = event.getUser();
        Iterator<String> it = SimplifiedDatabase.getData().keys();
        it.forEachRemaining(s -> {
            String sid =  SimplifiedDatabase.get(s);
            JSONObject data = Database.get(s);
            if(data != null && sid != null && sid.equals(user.getId())) {
                for (DataKey dataKey : DataKey.values()) {
                    if(dataKey.equals(DataKey.REWARDED)) continue;
                    data.remove(dataKey.toString());
                    data.put(dataKey.toString(), "Not Synced yet");
                }
                Database.set(s, data);
                SimplifiedDatabase.set(s, "Not Synced yet");
            }
        });
    }


}
