package com.rocket.rocketbot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public class Bot {

    private CommandClientBuilder cb;
    private RocketBot rocketBot;
    @Getter private EventWaiter eventWaiter;
    @Getter private CommandClient client;
    @Getter private JDA jda;

    public Bot(RocketBot rocketBot, EventWaiter waiter) {
        this.rocketBot = rocketBot;
        eventWaiter = waiter;
        cb = new CommandClientBuilder();
        if (start()) {
        }
    }

    private boolean start() {
        try {
            String token = rocketBot.getConfig().getBotToken();
            String trigger = rocketBot.getConfig().getCommandTrigger();
            String gameStr = rocketBot.getConfig().getDefaultGame();
            if (token == null || token.isEmpty()) {
                rocketBot.getLogger().log(Level.WARNING, "No token was provided. Please provide a valid token. Bot will not be able to start.");
                return false;
            } else if (token.equals("sample.token")) {
                rocketBot.getLogger().log(Level.WARNING, "Token was left at default. Please provide a valid token. Bot will not be able to start.");
                return false;
            }
            JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(token);
            Game game;
            if (gameStr != null && !gameStr.equals("default") && !gameStr.isEmpty()) {
                game = Game.playing(gameStr);
            } else {
                game = Game.playing("Type " + trigger + "help");
            }
            builder.setGame(game);
            cb.setGame(game);
            jda = builder.buildAsync();
        } catch (LoginException e) {
            rocketBot.getLogger().log(Level.WARNING, "The provided bot token was invalid");
            return false;
        }
        return true;
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
    }

}
