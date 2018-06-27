package com.rocket.rocketbot.commands.minecraftCommands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.Authentication.AuthSession;
import com.rocket.rocketbot.accountSync.Authentication.AuthToken;
import com.rocket.rocketbot.commands.BCommand;
import com.rocket.rocketbot.utils.FinderUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class Synchronize extends BCommand {

    private static final String CANCEL = "\u274c";

    public Synchronize(RocketBot rocketBot) {
        super(rocketBot, "/synchronize", "rocket.synchronize", "sync", "discord", "link");
        this.isPlayerOnly = true;
        this.arg = "<Discord Username or Discord ID>";
    }

    @Override
    protected void doCommand(CommandSender commandSender, String[] args) {
        JDA jda = RocketBot.getInstance().getBot().getJda();
        User dUser = StringUtils.isNumeric(args[0]) ? jda.getUserById(args[0]) : null;
        if (dUser == null)
            dUser = (FinderUtils.findMember(args[0]) == null) ? null : FinderUtils.findMember(args[0]).getUser();
        if (dUser != null) {
            if(FinderUtils.findPlayerInDatabase(dUser.getId()) != null) {
                ProxiedPlayer pp = FinderUtils.findPlayerInDatabase(dUser.getId());
                String message = RocketBot.getLocale().getTranslatedMessage("sync.synchronized").f(args[0], pp.getName());
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &r" + message));
                return;
            }
            AuthSession authSession = new AuthSession((ProxiedPlayer) commandSender, dUser);
            AuthToken token = authSession.getAuthToken();
            User finalDUser1 = dUser;
            EventWaiter eventWaiter = RocketBot.getInstance().getEventWaiter();
            dUser.openPrivateChannel().queue(pc -> pc.sendMessage(verificationCode(token)).queue(m -> {
                m.addReaction(CANCEL).complete();
                eventWaiter.waitForEvent(MessageReactionAddEvent.class, e -> e.getReaction().getReactionEmote().getName().equals(CANCEL) && e.getMessageId().equals(m.getId()) && !e.getUser().isBot(), a -> {
                    a.getReaction().removeReaction().queue();
                    authSession.cancel();
                }, AuthSession.SYNC_TIMEOUT, TimeUnit.MINUTES, authSession::cancel);
                String msg = RocketBot.getLocale().getTranslatedMessage("sync.pending").finish();
                commandSender.sendMessage((ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &r" + msg)));
                msg = RocketBot.getLocale().getTranslatedMessage("sync.sent").f(finalDUser1.getName() + "(" + finalDUser1.getId() + ")");
                commandSender.sendMessage((ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &r" + msg)));
            }, t -> {
                commandSender.sendMessage((ChatColor.translateAlternateColorCodes('&', cannotSendCode())));
                authSession.cancel();
            }), t -> {
                commandSender.sendMessage((ChatColor.translateAlternateColorCodes('&', cannotSendCode())));
                authSession.cancel();
            });
        } else {
            String msg = RocketBot.getLocale().getTranslatedMessage("sync.not-found").finish();
            commandSender.sendMessage((ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &r" + msg)));
        }
    }

    private String verificationCode(AuthToken token) {
        return RocketBot.getLocale().getTranslatedMessage("sync.message").f(token.toString());
    }

    private String cannotSendCode() {
        String msg = RocketBot.getLocale().getTranslatedMessage("sync.cannot-send").finish();
        return "&6[RocketBot] &r" + msg;
    }
}
