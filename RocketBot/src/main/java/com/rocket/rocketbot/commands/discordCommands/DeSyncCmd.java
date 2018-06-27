package com.rocket.rocketbot.commands.discordCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import com.rocket.rocketbot.commands.DCommand;
import com.rocket.rocketbot.utils.FinderUtils;
import net.dv8tion.jda.core.entities.Member;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class DeSyncCmd extends DCommand {

    public DeSyncCmd(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "desynchronize";
        this.help = "Desynchronize an account";
        this.arguments = "<Discord user name or ID>";
        this.aliases = new String[]{"desync"};
        this.category = Bot.Categories.ADMIN.getCategory();
    }

    @Override
    protected void doCommand(CommandEvent e) {
        Member member = FinderUtils.findMember(e.getArgs());
        if(member == null)
            return;
        ProxiedPlayer pp = FinderUtils.findPlayerInDatabase(member.getUser().getId());
        if(pp == null)
            return;
        Database.getJSONObject(pp.getUniqueId().toString()).remove(DataKey.DISCORD_ID.toString());
        Database.getJSONObject(pp.getUniqueId().toString()).put(DataKey.DISCORD_ID.toString(), "Not Synced Yet");
        Database.getJSONObject(pp.getUniqueId().toString()).remove(DataKey.DISCORD_USERNAME.toString());
        Database.getJSONObject(pp.getUniqueId().toString()).put(DataKey.DISCORD_USERNAME.toString(), "Not Synced Yet");
        SimplifiedDatabase.set(pp.getUniqueId().toString(), "Not Synced Yet");
    }
}
