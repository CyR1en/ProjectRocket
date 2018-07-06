package com.rocket.rocketbot.commands.minecraftCommands;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.Authentication.AuthManager;
import com.rocket.rocketbot.accountSync.Authentication.AuthSession;
import com.rocket.rocketbot.accountSync.Authentication.AuthToken;
import com.rocket.rocketbot.accountSync.exceptions.IllegalConfirmKeyException;
import com.rocket.rocketbot.commands.BCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SyncConfirm extends BCommand {

    public SyncConfirm(RocketBot rocketBot) {
        super(rocketBot, "syncconfirm", "rocket.synchronize", "sc", "codigo", "verify");
        this.isPlayerOnly = true;
        this.arg = "<confirm code>";
    }

    @Override
    protected void doCommand(CommandSender commandSender, String[] args) {
        AuthToken authToken;
        try {
            authToken = new AuthToken((ProxiedPlayer) commandSender, args[0]);
            AuthManager authManager = RocketBot.getInstance().getAuthManager();
            AuthSession authSession = authManager.getSession(authToken.toString());
            authSession.authorize((ProxiedPlayer) commandSender, authToken);
        } catch (IllegalConfirmKeyException e) {
            String message = RocketBot.getLocale().getTranslatedMessage("sync.invalid-code").finish();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6[RocketBot] &c" + message));
        }
    }
}
