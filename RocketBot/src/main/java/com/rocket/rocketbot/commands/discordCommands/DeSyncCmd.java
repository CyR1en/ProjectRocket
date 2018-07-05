package com.rocket.rocketbot.commands.discordCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import com.rocket.rocketbot.commands.DCommand;
import com.rocket.rocketbot.entity.Messenger;
import com.rocket.rocketbot.utils.FinderUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.managers.GuildController;
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
        Member member = FinderUtils.findMember(e.getArgs());
        if(member == null) {
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-c1").f(e.getArgs());
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.WARNING), autoDeleteConsumer(e));
            return;
        }
        ProxiedPlayer pp = FinderUtils.findPlayerInDatabase(member.getUser().getId());
        if(pp == null) {
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-c2").f(e.getArgs());
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.WARNING), autoDeleteConsumer(e));
            return;
        }
        try {
            String uuid = pp.getUniqueId().toString();
            JSONObject data = Database.get(uuid);
            if(data == null)
                return;
            GuildController controller = e.getGuild().getController();
            String id = data.getString(DataKey.MC_GROUP.toString());
            e.getGuild().getRolesByName(id, true).forEach(r -> controller.removeRolesFromMember(member, r).queue());
            if(member.getEffectiveName().equals(pp.getName()))
                controller.setNickname(member, "").queue();
            data.remove(DataKey.DISCORD_ID.toString());
            data.put(DataKey.DISCORD_ID.toString(), "Not Synced Yet");
            data.remove(DataKey.DISCORD_USERNAME.toString());
            data.put(DataKey.DISCORD_USERNAME.toString(), "Not Synced Yet");
            Database.set(uuid, data);
            SimplifiedDatabase.set(uuid, "Not Synced Yet");
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-c3").f(member.getEffectiveName());
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.SUCCESS));
        } catch (Exception ex) {
            String msg = RocketBot.getLocale().getTranslatedMessage("dcommand.dsync-fail").finish();
            e.reply(Messenger.embedMessage(e, msg, Messenger.ResponseLevel.ERROR), autoDeleteConsumer(e));
        }

    }
}
