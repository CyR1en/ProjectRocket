package com.rocket.rocketbot.commands.discordCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import com.rocket.rocketbot.commands.DCommand;
import com.rocket.rocketbot.entity.Messenger;
import com.rocket.rocketbot.events.DeSynchronizeEvent;
import com.rocket.rocketbot.utils.Finder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.managers.GuildController;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;

public class DeSyncCmd extends DCommand {

    public DeSyncCmd(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "desynchronize";
        this.help = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-d").finish();
        this.arguments = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-a").finish();
        this.aliases = new String[]{"desync"};
        this.category = Bot.Categories.ADMIN.getCategory();
        this.type = Type.EMBED;
    }

    @Override
    protected void doCommand(CommandEvent e) {
        Member member = Finder.findMember(e.getArgs());
        if (member == null) {
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-c1").f(e.getArgs());
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.WARNING), autoDeleteConsumer(e));
            return;
        }
        ProxiedPlayer pp = Finder.findPlayerInDatabase(member.getUser().getId());
        if (pp == null) {
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-c2").f(e.getArgs());
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.WARNING), autoDeleteConsumer(e));
            return;
        }
        try {
            String key = pp.getName();
            JSONObject data = Database.get(key);
            if (data == null)
                return;
            GuildController controller = e.getGuild().getController();
            String id = data.getString(DataKey.MC_GROUP.toString());
            e.getGuild().getRolesByName(id, true).forEach(r -> controller.removeRolesFromMember(member, r).queue());
            if (member.getEffectiveName().equals(pp.getName()))
                controller.setNickname(member, "").queue();
            for (DataKey dataKey : DataKey.values()) {
                if (dataKey.equals(DataKey.REWARDED))
                    continue;
                data.remove(dataKey.toString());
                data.put(dataKey.toString(), "Not Synced yet");
            }
            Database.set(key, data);
            SimplifiedDatabase.set(key, "Not Synced yet");
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-c3").f(member.getEffectiveName());
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.SUCCESS));
            ProxyServer.getInstance().getPluginManager().callEvent(new DeSynchronizeEvent(pp));
        } catch (Exception ex) {
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-fail").finish();
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.ERROR), autoDeleteConsumer(e));
        }

    }
}
