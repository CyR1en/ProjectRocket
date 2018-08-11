package com.rocket.rocketbot.commands.minecraftCommands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.Authentication.AuthSession;
import com.rocket.rocketbot.accountSync.Authentication.AuthToken;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import com.rocket.rocketbot.commands.BCommand;
import com.rocket.rocketbot.utils.Finder;
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
        ProxiedPlayer pp = (ProxiedPlayer) commandSender;
        if(getRocketBot().getAuthManager().inSession(pp)) {
            String message = RocketBot.getLocale().getTranslatedMessage("sync.in-session").finish();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &c" + message));
            return;
        }
        String s = SimplifiedDatabase.get(pp.getName());
        User dUser;
        if (s != null && !s.equals("Not Synced yet")) {
            dUser = getRocketBot().getBot().getJda().getUserById(SimplifiedDatabase.get(pp.getName()));
            String message = RocketBot.getLocale().getTranslatedMessage("sync.synchronized-1").f(dUser.getName());
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &c" + message));
            return;
        }
        dUser = StringUtils.isNumeric(args[0]) ? jda.getUserById(args[0]) : null;
        if (dUser == null)
            dUser = (Finder.findMember(args[0]) == null) ? null : Finder.findMember(args[0]).getUser();
        if (dUser != null) {
            if(Finder.findPlayerInDatabase(dUser.getId()) != null) {
                ProxiedPlayer p1 = Finder.findPlayerInDatabase(dUser.getId());
                String message = RocketBot.getLocale().getTranslatedMessage("sync.synchronized").f(args[0], p1.getName());
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &c" + message));
                return;
            }
            AuthSession authSession = new AuthSession((ProxiedPlayer) commandSender, dUser);
            AuthToken token = authSession.getAuthToken();
            User finalDUser1 = dUser;
            EventWaiter eventWaiter = RocketBot.getInstance().getEventWaiter();
            dUser.openPrivateChannel().queue(pc -> pc.sendMessage(verificationCode(token)).queue(m -> {
                m.addReaction(CANCEL).complete();
                eventWaiter.waitForEvent(MessageReactionAddEvent.class, e -> e.getReaction().getReactionEmote().getName().equals(CANCEL) && e.getMessageId().equals(m.getId()) && !e.getUser().isBot(), a -> {
                    authSession.cancel();
                    m.getChannel().sendMessage(RocketBot.getLocale().getTranslatedMessage("session.cancelled").f("")).queue();
                }, AuthSession.SYNC_TIMEOUT, TimeUnit.MINUTES, authSession::cancel);
                String msg = RocketBot.getLocale().getTranslatedMessage("sync.pending").finish();
                commandSender.sendMessage((ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &a" + msg)));
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
            commandSender.sendMessage((ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] &c" + msg)));
        }
    }

    private String verificationCode(AuthToken token) {
        return RocketBot.getLocale().getTranslatedMessage("sync.message").f(token.toString());
    }

    private String cannotSendCode() {
        String msg = RocketBot.getLocale().getTranslatedMessage("sync.cannot-send").finish();
        return "&6[RocketBot] &c" + msg;
    }
}
