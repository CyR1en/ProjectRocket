package com.rocket.rocketbot.commands;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.utils.SRegex;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.regex.Pattern;

public abstract class BCommand extends Command {

    @Getter private RocketBot rocketBot;

    protected boolean isPlayerOnly;
    protected String arg;

    public BCommand(RocketBot rocketBot, String name) {
        super(name);
        this.rocketBot = rocketBot;
        isPlayerOnly = false;
        arg = "";
    }

    public BCommand(RocketBot rocketBot, String name, String perms, String... alias) {
        super(name, perms, alias);
        this.rocketBot = rocketBot;
        arg = "";
    }

    protected void sendMessage(CommandSender commandSender, TextComponent msg) {
        commandSender.sendMessage(msg);
    }

    protected void sendErrMessage(CommandSender commandSender, TextComponent msg) {
        BaseComponent bc = msg.duplicate();
        bc.retain(ComponentBuilder.FormatRetention.NONE);
        String s = bc.toPlainText();
        msg = new TextComponent(ChatColor.RED + s);
        commandSender.sendMessage(msg);
    }

    private int countRequiredArgs() {
        if (arg == null)
            return 0;
        String raw = arg;
        if (raw.endsWith("...") || raw.endsWith("...>"))
            return -1;
        String noOptionals = raw.replaceAll("\\[.*?]", "").trim();
        List<String> requiredArgs = new SRegex(noOptionals).find(Pattern.compile("<.*?>")).getResultsList();
        return requiredArgs.size();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        int countedReqArgs = countRequiredArgs();
        int countedArgsProvided = args == null || args.length == 0 ? 0 : args.length;
        if ((countedReqArgs > countedArgsProvided || countedReqArgs < countedArgsProvided) && countRequiredArgs() != -1) {
            String msg = RocketBot.getLocale().getTranslatedMessage("mcommand.invalid-args").f(this.getName(), arg);
            sendErrMessage(commandSender, new TextComponent(msg));
            return;
        }
        if (isPlayerOnly)
            if (!(commandSender instanceof ProxiedPlayer)) {
                String msg = RocketBot.getLocale().getTranslatedMessage("mcommand.player-only").finish();
                sendErrMessage(commandSender, new TextComponent(msg));
                return;
            }
        doCommand(commandSender, args);
    }

    protected abstract void doCommand(CommandSender commandSender, String[] args);
}
