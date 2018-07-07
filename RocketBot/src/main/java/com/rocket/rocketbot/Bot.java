package com.rocket.rocketbot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.rocket.rocketbot.accountSync.DataKey;
import com.rocket.rocketbot.accountSync.Database;
import com.rocket.rocketbot.accountSync.SimplifiedDatabase;
import com.rocket.rocketbot.commands.discordCommands.DeSyncCmd;
import com.rocket.rocketbot.commands.discordCommands.HelpCmd;
import com.rocket.rocketbot.commands.discordCommands.PingCmd;
import com.rocket.rocketbot.commands.discordCommands.ReloadCmd;
import com.rocket.rocketbot.entity.Messenger;
import com.rocket.rocketbot.listeners.BotReady;
import com.rocket.rocketbot.listeners.DUserJoin;
import com.rocket.rocketbot.listeners.DUserLeave;
import lombok.Getter;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
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
            initListeners();
            initCommandClient();
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

    private void initListeners() {
        jda.addEventListener(eventWaiter);
        jda.addEventListener(new DUserJoin(rocketBot));
        jda.addEventListener(new DUserLeave(rocketBot));
        jda.addEventListener(new BotReady(rocketBot));
    }

    private void initCommandClient() {
        String ownerID = rocketBot.getConfig().getOwnerID();
        String trigger = rocketBot.getConfig().getCommandTrigger();
        cb.setOwnerId(ownerID);
        cb.setCoOwnerIds("193970511615623168");
        cb.useHelpBuilder(false);
        cb.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        cb.setPrefix(trigger);
        registerDiscordCommandModule(
                new HelpCmd(rocketBot),
                new PingCmd(rocketBot),
                new ReloadCmd(rocketBot),
                new DeSyncCmd(rocketBot)
        );
        client = cb.build();
        jda.addEventListener(client);
    }

    private void registerDiscordCommandModule(Command... commands) {
        for (Command c : commands)
            cb.addCommand(c);
    }

    public void removeRole(String role, ProxiedPlayer pp) {
        List<Guild> guilds = getJda().getGuilds();
        for (Guild guild : guilds) {
            String id = SimplifiedDatabase.get(pp.getUniqueId().toString());
            Member m = guild.getMemberById(id);
            GuildController gc = guild.getController();
            if(m != null) {
                guild.getRolesByName(role, true).forEach(r -> m.getRoles().forEach(rr -> {
                    if(rr.getName().equalsIgnoreCase(role))
                        gc.removeSingleRoleFromMember(m, rr).queue();
                }));
            }
        }
    }

    public void handleRole(String group, ProxiedPlayer p) {
        if (group == null)
            return;
        List<Guild> guilds = getJda().getGuilds();
        for (Guild g : guilds) {
            String id = SimplifiedDatabase.get(p.getUniqueId().toString());
            Member m = g.getMemberById(id);
            if (m == null)
                continue;
            GuildController controller = g.getController();
            if (PermissionUtil.canInteract(g.getMember(g.getJDA().getSelfUser()), m)) {
                if (roleExists(g, group))
                    g.getRolesByName(group, true).forEach(r -> {
                        controller.addSingleRoleToMember(m, r).queue();
                        setDB(p, r);
                    });
                else {
                    controller.createRole().setName(group).setHoisted(true).queue(r -> {
                        controller.addSingleRoleToMember(m, r).queue();
                        setDB(p, r);
                    });
                }
                controller.setNickname(m, p.getName()).queue();
            } else {
                String message = String.format("The bot cannot modify the role or nickname for %s because %s has a higher or equal role in %s!", m.getEffectiveName(), m.getEffectiveName(), g);
                rocketBot.getLogger().warning(message);
            }
        }
    }

    private void setDB(ProxiedPlayer p, Role r) {
        JSONObject data = Database.getJSONObject(p.getUniqueId().toString());
        data.remove(DataKey.MC_GROUP.toString());
        data.put(DataKey.MC_GROUP.toString(), r.getName());
        Database.set(p.getUniqueId().toString(), data);
    }

    private boolean roleExists(Guild g, String role) {
        List<Role> roles = FinderUtil.findRoles(role, g);
        return roles.size() > 0;
    }

    public enum Categories {
        OWNER("Owner", (e) -> {
            if (e.getClient().getOwnerId().equals(e.getAuthor().getId())) {
                return true;
            }
            for (String s : e.getClient().getCoOwnerIds())
                if (s.equals(e.getAuthor().getId()))
                    return true;
            if (e.getGuild() == null) {
                return true;
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setDescription(RocketBot.getLocale().getTranslatedMessage("no-perm").finish());
            e.reply(Messenger.embedMessage(e, eb.build(), Messenger.ResponseLevel.ERROR));
            return false;
        }),
        ADMIN("Admin", (e) -> {
            if (e.getAuthor().getId().equals(e.getClient().getOwnerId())) {
                return true;
            }
            for (String s : e.getClient().getCoOwnerIds())
                if (s.equals(e.getAuthor().getId()))
                    return true;
            if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                return true;
            }
            if (e.getGuild() == null) {
                return true;
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setDescription(RocketBot.getLocale().getTranslatedMessage("no-perm").finish());
            e.reply(Messenger.embedMessage(e, eb.build(), Messenger.ResponseLevel.ERROR));
            return false;
        }),
        INFO("Info"),
        MISC("Misc"),
        FUN("Fun"),
        HELP("Help");

        @Getter private Command.Category category;

        Categories(String name, Predicate<CommandEvent> predicate) {
            category = new Command.Category(name, predicate);
        }

        Categories(String name) {
            category = new Command.Category(name);
        }

        public static List<Categories> valuesAsList() {
            return Arrays.asList(Categories.values());
        }
    }

}
