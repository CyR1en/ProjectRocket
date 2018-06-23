package com.rocket.rocketbot.commands;

import com.rocket.rocketbot.RocketBot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class BCommand extends Command {

    private RocketBot rocketBot;
    protected boolean isPlayerOnly;

    public BCommand(RocketBot rocketBot, String name) {
        super(name);
        this.rocketBot = rocketBot;
        isPlayerOnly = false;
    }

    public BCommand(RocketBot rocketBot, String name, String perms, String... alias) {
        super(name, perms, alias);
        this.rocketBot = rocketBot;
    }

    protected void sendMessage(CommandSender commandSender, TextComponent msg) {
        commandSender.sendMessage(msg);
    }

    protected void sendErrMessage(CommandSender commandSender, TextComponent msg) {
        BaseComponent bc = msg.duplicateWithoutFormatting();
        String s = bc.toPlainText();
        msg = new TextComponent(ChatColor.RED + s);
        commandSender.sendMessage(msg);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(isPlayerOnly)
            if(!(commandSender instanceof ProxiedPlayer)) {
                String msg = "Only players can use this command.";
                sendErrMessage(commandSender, new TextComponent(msg));
                return;
            }
        doCommand(commandSender, strings);
    }

    protected abstract void doCommand(CommandSender commandSender, String[] strings);
}
