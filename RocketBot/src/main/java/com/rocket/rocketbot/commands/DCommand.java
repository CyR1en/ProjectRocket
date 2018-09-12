package com.rocket.rocketbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.configuration.SConfig;
import com.rocket.rocketbot.entity.Messenger;
import com.rocket.rocketbot.utils.SRegex;
import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public abstract class DCommand extends Command implements Comparable<Command> {

    protected static final long RESPONSE_DURATION = 5;

    protected final ScheduledExecutorService scheduler;

    protected boolean auto;
    protected SConfig config;

    @Getter protected Type type;
    @Getter protected boolean hidden;
    @Getter protected RocketBot rocketBot;

    public DCommand(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
        this.guildOnly = true;
        this.helpBiConsumer = (ce, c) -> ce.reply(getHelpCard(ce, c));
        this.botPermissions = setupPerms();
        config = rocketBot.getConfig();
        type = Type.DEFAULT;
        hidden = false;
        auto = config.isAutoDelete();
        scheduler = rocketBot.getScheduler();
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            int countedReqArgs = countRequiredArgs();
            int countedArgsProvided = event.getArgs().isEmpty() ? 0 : event.getArgs().split(" ").length;
            if ((countedReqArgs > countedArgsProvided || countedReqArgs < countedArgsProvided) && countRequiredArgs() != -1) {
                event.reply(invalidArgumentsMessageEmbed());
                event.reply(getHelpCard(event, this));
                return;
            }
            doCommand(event);
        } catch (Exception ex) {
            ex.printStackTrace();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(baos));
            byte[] out = baos.toByteArray();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("A fatal error occurred before " + this.name + " command finished execution.");
            eb.setDescription(ex.getClass().getSimpleName());
            eb.addField("StackTrace", "Send a report to the Dev! Click the stacktrace that have been sent along side this message to download the stacktrace.", false);
            MessageEmbed formatted = Messenger.embedMessage(event, eb.build(), Messenger.ResponseLevel.ERROR);
            event.reply("Generating stacktrace...", (msg) -> scheduler.schedule(() -> {
                msg.delete().queue();
                event.getTextChannel().sendFile(out, "stacktrace.txt", new MessageBuilder().setEmbed(formatted).build()).queue();
            }, 1, TimeUnit.SECONDS));
        }
    }


    //new RespondAPI below. Omit above methods after
    public void respond(CommandEvent event, String message, long duration, TimeUnit timeUnit, Messenger.ResponseLevel level) {
        Consumer<Message> consumer = auto ? (msg) -> scheduler.schedule(() -> {
            msg.delete().queue();
            event.getMessage().delete().queue();
        }, duration, timeUnit) : msg -> {
        };
        if (type == Type.EMBED) {
            level = level == null ? Messenger.ResponseLevel.INFO : level;
            event.reply(Messenger.embedMessage(event, message, level), consumer);
        } else
            event.reply(message, consumer);
    }

    public void respond(CommandEvent event, MessageEmbed messageEmbed, long duration, TimeUnit timeUnit) {
        Consumer<Message> consumer = auto ? (msg) -> scheduler.schedule(() -> {
            msg.delete().queue();
            event.getMessage().delete().queue();
        }, duration, timeUnit) : msg -> {
        };
        event.reply(messageEmbed, consumer);
    }

    protected abstract void doCommand(CommandEvent e);

    protected static MessageEmbed getHelpCard(CommandEvent e, Command c) {
        EmbedBuilder eb = new EmbedBuilder().setColor(e.getGuild().getMember(e.getJDA().getSelfUser()).getColor());
        eb.setTitle(c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1) + " Command Help Card:", null);
        String argument = c.getArguments() == null ? "" : c.getArguments();
        eb.addField("Usage", e.getClient().getPrefix() + c.getName() + " " + argument, false);
        eb.addField("Description", c.getHelp(), false);
        String r;
        if (c.getAliases().length == 0)
            r = "This command does not have any alias";
        else
            r = Arrays.toString(c.getAliases()).replaceAll("\\[", "").replaceAll("]", "");
        eb.addField("Alias", r, false);
        String permission = c.getUserPermissions().length < 1 ? "None" : Arrays.toString(c.getUserPermissions());
        if (c.isOwnerCommand())
            permission = "OWNER";
        eb.addField("Permission", "Required Permission: " + permission, false);
        return eb.build();
    }

    private String noPermissionMessage() {
        return "You don't have permission to execute this command";
    }

    protected MessageEmbed noPermissionMessageEmbed() {
        String s = noPermissionMessage();
        EmbedBuilder eb = new EmbedBuilder().setTitle(s, null);
        eb.setColor(Color.RED);
        return eb.build();
    }

    private String invalidArgumentsMessage() {
        return "You've provided invalid arguments";
    }

    private MessageEmbed invalidArgumentsMessageEmbed() {
        String s = invalidArgumentsMessage();
        EmbedBuilder eb = new EmbedBuilder().setTitle(s, null);
        eb.setColor(Messenger.ResponseLevel.ERROR.getColor());
        return eb.build();
    }

    private int countRequiredArgs() {
        if (this.getArguments() == null)
            return 0;
        String raw = this.getArguments();
        if (raw.endsWith("...") || raw.endsWith("...>"))
            return -1;
        String noOptionals = raw.replaceAll("\\[.*?]", "").trim();
        java.util.List<String> requiredArgs = new SRegex(noOptionals).find(Pattern.compile("<.*?>")).getResultsList();
        return requiredArgs.size();
    }

    protected Consumer<Message> autoDeleteConsumer(CommandEvent event) {
        return auto ? (msg) -> scheduler.schedule(() -> {
            msg.delete().queue();
            event.getMessage().delete().queue();
        }, RESPONSE_DURATION, TimeUnit.MINUTES) : msg -> {
        };
    }

    private Permission[] setupPerms() {
        return new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_HISTORY};
    }

    public int compareTo(Command o) {
        return this.getName().compareTo(o.getName());
    }

    public enum Type {
        EMBED, DEFAULT
    }
}
