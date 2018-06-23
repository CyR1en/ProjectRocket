package com.rocket.rocketbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.configuration.SConfig;
import com.rocket.rocketbot.utils.SRegex;
import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;

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
    @Getter protected RocketBot rocketBot;

    public DCommand(RocketBot rocketBot) {
        this.rocketBot = rocketBot;
        this.guildOnly = true;
        this.helpBiConsumer = (ce, c) -> respond(ce, getHelpCard(ce, c));
        this.botPermissions = setupPerms();
        config = rocketBot.getConfig();
        type = Type.DEFAULT;
        auto = config.isAutoDelete();
        scheduler = rocketBot.getScheduler();
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            int countedReqArgs = countRequiredArgs();
            int countedArgsProvided = event.getArgs().isEmpty() ? 0 : event.getArgs().split(" ").length;
            if ((countedReqArgs > countedArgsProvided || countedReqArgs < countedArgsProvided) && countRequiredArgs() != -1) {
                respond(event, invalidArgumentsMessageEmbed());
                respond(event, getHelpCard(event, this));
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
            MessageEmbed formatted = embedMessage(event, eb.build(), ResponseLevel.LEVEL_3);
            respond(event, "Generating stacktrace...", (msg) -> scheduler.schedule(() -> {
                msg.delete().queue();
                event.getTextChannel().sendFile(out, "stacktrace.txt", new MessageBuilder().setEmbed(formatted).build()).queue();
            }, 1, TimeUnit.SECONDS));
        }
    }

    public void respond(CommandEvent event, String message) {
        respond(null, event, message);
    }

    public void respond(ResponseLevel level, CommandEvent event, String message) {
        if (type == Type.EMBED) {
            level = level == null ? ResponseLevel.DEFAULT : level;
            respond(event, embedMessage(event, message, level), RESPONSE_DURATION, TimeUnit.MINUTES);
        } else
            respond(event, message, RESPONSE_DURATION, TimeUnit.MINUTES);
    }

    public void respond(CommandEvent event, MessageEmbed messageEmbed) {
        respond(event, messageEmbed, RESPONSE_DURATION, TimeUnit.MINUTES);
    }

    public RestAction<Message> respond(MessageEmbed messageEmbed, CommandEvent event) {
        return event.getTextChannel().sendMessage(messageEmbed);
    }

    public RestAction<Message> respond(String message, CommandEvent event) {
        return respond(null, message, event);
    }

    public RestAction<Message> respond(ResponseLevel level, String message, CommandEvent event) {
        if (type == Type.EMBED) {
            level = level == null ? ResponseLevel.DEFAULT : level;
            return event.getTextChannel().sendMessage(embedMessage(event, message, level));
        } else
            return event.getTextChannel().sendMessage(message);
    }

    public void respond(CommandEvent event, String message, Consumer<Message> consumer) {
        respond(null, message, event).queue(consumer);
    }

    public void respond(CommandEvent event, MessageEmbed messageEmbed, Consumer<Message> consumer) {
        respond(messageEmbed, event).queue(consumer);
    }

    public void respond(CommandEvent event, String message, long duration, TimeUnit timeUnit) {
        respond(event, message, duration, timeUnit, null);
    }

    public void respond(CommandEvent event, String message, ResponseLevel level) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(message);
        respond(event, embedMessage(event, eb.build(), level));
    }

    public void respond(CommandEvent event, MessageEmbed message, ResponseLevel level) {
        respond(event, embedMessage(event, message, level));
    }


    //new RespondAPI below. Omit above methods after
    public void respond(CommandEvent event, String message, long duration, TimeUnit timeUnit, ResponseLevel level) {
        Consumer<Message> consumer = auto ? (msg) -> scheduler.schedule(() -> {
            msg.delete().queue();
            event.getMessage().delete().queue();
        }, duration, timeUnit) : msg -> {
        };
        if (type == Type.EMBED) {
            level = level == null ? ResponseLevel.DEFAULT : level;
            event.reply(embedMessage(event, message, level), consumer);
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
        eb.setColor(ResponseLevel.LEVEL_3.getColor());
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

    private Permission[] setupPerms() {
        return new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_HISTORY};
    }

    protected MessageEmbed embedMessage(CommandEvent event, String message, ResponseLevel level, String footer) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        User bot = event.getJDA().getSelfUser();
        embedBuilder.setAuthor(bot.getName() + " #" + bot.getDiscriminator(),
                null, bot.getEffectiveAvatarUrl());
        embedBuilder.setDescription(message);
        footer = footer == null ? (level == null ? ResponseLevel.DEFAULT.getFooter() : level.getFooter()) : footer;
        embedBuilder.setFooter(footer, null);
        embedBuilder.setTimestamp(event.getMessage().getCreationTime());
        embedBuilder.setColor(getResponseColor(level, event.getGuild(), bot));
        return embedBuilder.build();
    }

    protected MessageEmbed embedMessage(CommandEvent event, String message, ResponseLevel level) {
        return embedMessage(event, message, level, null);
    }

    protected MessageEmbed embedMessage(CommandEvent event, MessageEmbed message, ResponseLevel level) {
        return embedMessage(event, message, level, null);
    }

    protected MessageEmbed embedMessage(CommandEvent event, MessageEmbed message, ResponseLevel level, String footer) {
        EmbedBuilder embedBuilder = new EmbedBuilder(message);
        User bot = event.getJDA().getSelfUser();
        embedBuilder.setAuthor(bot.getName() + "#" + bot.getDiscriminator(),
                null, bot.getEffectiveAvatarUrl());
        footer = footer == null ? (level == null ? ResponseLevel.DEFAULT.getFooter() : level.getFooter()) : footer;
        embedBuilder.setFooter(footer, null);
        embedBuilder.setTimestamp(event.getMessage().getCreationTime());
        embedBuilder.setColor(getResponseColor(level, event.getGuild(), bot));
        return embedBuilder.build();
    }

    private Color getResponseColor(ResponseLevel level, Guild g, User bot) {
        if (level == null) {
            return g.getMember(bot).getColor();
        }
        switch (level) {
            case LEVEL_1:
                return ResponseLevel.LEVEL_1.getColor();
            case LEVEL_2:
                return ResponseLevel.LEVEL_2.getColor();
            case LEVEL_3:
                return ResponseLevel.LEVEL_3.getColor();
            default:
                return g.getMember(bot).getColor();
        }
    }

    public int compareTo(Command o) {
        return this.getName().compareTo(o.getName());
    }

    public enum Type {
        EMBED, DEFAULT
    }

    public enum ResponseLevel {
        LEVEL_1 {
            public Color getColor() {
                return new Color(92, 184, 92);
            }

            public String getFooter() {
                return "Success";
            }
        }, LEVEL_2 {
            public Color getColor() {
                return new Color(243, 119, 54);
            }

            public String getFooter() {
                return "Warning";
            }
        }, LEVEL_3 {
            public Color getColor() {
                return new Color(188, 75, 79);
            }

            public String getFooter() {
                return "Error";
            }
        }, DEFAULT {
            public Color getColor() {
                return null;
            }

            public String getFooter() {
                return "Response";
            }
        };

        public abstract Color getColor();

        public abstract String getFooter();


    }
}
