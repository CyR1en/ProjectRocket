package com.rocket.rocketbot.accountSync.Authentication;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.accountSync.exceptions.IllegalConfirmKeyException;
import com.rocket.rocketbot.accountSync.exceptions.IllegalConfirmRequesterException;
import com.rocket.rocketbot.accountSync.exceptions.IllegalConfirmSessionIDException;
import com.rocket.rocketbot.entity.DUser;
import com.rocket.rocketbot.entity.UnifiedUser;
import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AuthSession {

    public static final long SYNC_TIMEOUT = 5;

    @Getter private ProxiedPlayer mcAcc;
    @Getter private User DiscordAcc;
    @Getter private AuthToken authToken;
    @Getter private Status status;
    private AuthManager authManager;
    private ScheduledExecutorService scheduler;
    private String messageID;

    @Getter private final String sessionID = RandomStringUtils.randomNumeric(6);

    public AuthSession(ProxiedPlayer mcAcc, User discordAcc, AuthManager authManager) {
        this.mcAcc = mcAcc;
        this.DiscordAcc = discordAcc;
        this.authManager = authManager;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::cancel, SYNC_TIMEOUT, TimeUnit.MINUTES);
        authToken = new AuthToken(sessionID, mcAcc );
        authManager.addSession(this);
        status = Status.PENDING;
        mcbSyncLog(SyncMessage.PENDING);
    }

    public AuthSession(ProxiedPlayer mcAcc, User discordAcc) {
        this(mcAcc, discordAcc, RocketBot.getInstance().getAuthManager());
    }

    public void cancel() {
        if(status == Status.PENDING) {
            authManager.removeSession(this.authToken.toString());
            status = Status.CANCELLED;
            mcbSyncLog(SyncMessage.CANCELLED);
            String msg = RocketBot.getLocale().getTranslatedMessage("sync.cancelled").finish();
            mcAcc.chat(ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] " + msg));
        }
        scheduler.shutdownNow();
    }

    public void authorize(ProxiedPlayer sender, AuthToken token) {
        boolean authenticated = false;
        if(token.getMcAcc().getUniqueId().equals(sender.getUniqueId())) {
            try {
                authenticated = authToken.authenticateToken(token);
            } catch (IllegalConfirmRequesterException illegalConfirmRequester) {
                sender.chat(ChatColor.translateAlternateColorCodes('&',"&6[RocketBot] &c" + illegalConfirmRequester.getMsg()));
                mcbSyncLog(SyncMessage.DECLINED, illegalConfirmRequester.getClass().getSimpleName());
            } catch (IllegalConfirmKeyException illegalConfirmKey) {
                sender.chat(ChatColor.translateAlternateColorCodes('&',"&6[RocketBot] &c" + illegalConfirmKey.getMsg()));
                mcbSyncLog(SyncMessage.DECLINED, illegalConfirmKey.getClass().getSimpleName());
            } catch (IllegalConfirmSessionIDException illegalConfirmSessionID) {
                sender.chat(ChatColor.translateAlternateColorCodes('&',"&6[RocketBot] &c" + illegalConfirmSessionID.getMsg()));
                mcbSyncLog(SyncMessage.DECLINED, illegalConfirmSessionID.getClass().getSimpleName());
            }
        } else {
            try {
                throw new IllegalConfirmRequesterException();
            } catch (IllegalConfirmRequesterException illegalConfirmRequester) {
                sender.chat("&6[RocketBot] &c" + illegalConfirmRequester.getMsg());
            }
        }
        status = authenticated ?  Status.APPROVED : Status.DENIED;
        if(status == Status.APPROVED) {
            String msg = RocketBot.getLocale().getTranslatedMessage("sync.cancelled").finish();
            getMcAcc().chat(ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] " + msg));
            mcbSyncLog(SyncMessage.APPROVED);
            UnifiedUser mcUser = new UnifiedUser(sender);
            DUser mcbUser = new DUser(authManager.getSession(this.authToken.toString()).getDiscordAcc());
            mcUser.setMcbUser(mcbUser);
            Database.set(mcUser.getProxiedPlayer().getUniqueId().toString(), new JSONObject(mcUser.getDataAsMap()));
            authManager.removeSession(this.getSessionID());
        } else {
            String msg = RocketBot.getLocale().getTranslatedMessage("sync.cancelled").finish();
            getMcAcc().chat(ChatColor.translateAlternateColorCodes('&', "&6[RocketBot] " + msg));
            authManager.removeSession(this.getSessionID());
        }
    }

    private void mcbSyncLog(SyncMessage syncMessage, Object ... args) {
        Logger syncLogger = RocketBot.getInstance().getLogger();
        switch (syncMessage) {
            case PENDING:
                syncLogger.info(formatSyncMessage(SyncMessage.PENDING, getSessionID(), getMcAcc().getName(), getDiscordAcc().getName()));
                break;
            case CANCELLED:
                syncLogger.info(formatSyncMessage(SyncMessage.CANCELLED, getSessionID(), getMcAcc().getName(), getDiscordAcc().getName()));
                break;
            case APPROVED:
                syncLogger.info(formatSyncMessage(SyncMessage.APPROVED, getSessionID(), getMcAcc().getName(), getDiscordAcc().getName()));
                break;
            case DECLINED:
                syncLogger.info(formatSyncMessage(SyncMessage.DECLINED, getSessionID(), getMcAcc().getName(), getDiscordAcc().getName(), args));
                break;
        }
    }

    private String formatSyncMessage(SyncMessage syncMessage, Object ... args) {
        return String.format(syncMessage.toString(), args);
    }

    @Override
    public String toString() {
        return "Session by: " + getMcAcc().getDisplayName() +
                "\n Session ID: " + getSessionID() +
                "\n Sync request to " + getDiscordAcc() +
                "\n Status: " + getStatus();
    }

    private enum Status {
        PENDING,
        APPROVED,
        DENIED,
        CANCELLED
    }

    private enum SyncMessage {
        PENDING(RocketBot.getLocale().getTranslatedMessage("session.start").f(suffix())),
        APPROVED(RocketBot.getLocale().getTranslatedMessage("session.approved").f(suffix())),
        CANCELLED(RocketBot.getLocale().getTranslatedMessage("session.cancelled").f(suffix())),
        DECLINED(RocketBot.getLocale().getTranslatedMessage("session.declined").f(suffix()));

        private String message;

        SyncMessage(String message) {
            this.message = message;
        }

        static String suffix() {
            return (" Session ID: %s | Requested by: %s | Sync Request to: %s");
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
