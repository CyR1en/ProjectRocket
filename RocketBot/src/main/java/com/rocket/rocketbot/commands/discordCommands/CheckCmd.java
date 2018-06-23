package com.rocket.rocketbot.commands.discordCommands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import org.joda.time.DateTime;
import us.cyrien.seasonsmc.Bot;
import us.cyrien.seasonsmc.SeasonsTime;
import us.cyrien.seasonsmc.commands.DCommand;
import us.cyrien.seasonsmc.entity.Session;
import us.cyrien.seasonsmc.entity.TimeStamp;
import us.cyrien.seasonsmc.utils.HTTPUtils;
import us.cyrien.seasonsmc.utils.TimeStampUtil;
import us.cyrien.seasonsmc.utils.UUIDFetcher;

import java.io.IOException;
import java.util.UUID;

public class CheckCmd extends DCommand {

    public CheckCmd(SeasonsTime st) {
        super(st);
        this.name = "check";
        this.aliases = new String[]{"lookup", "time"};
        this.arguments = "<player name or UUID>";
        this.help = "Lookup connection details for a player";
        this.category = Bot.INFO;
        this.type = Type.EMBED;
    }

    @Override
    protected void doCommand(CommandEvent e) {
        if (e.getArgs().isEmpty()) {
            respond(ResponseLevel.LEVEL_3, "Invalid Argument", e).queue();
            return;
        }

        String s = null;
        try {
            s = HTTPUtils.performGetRequest(HTTPUtils.constantURL("https://api.mojang.com/users/profiles/minecraft/" + e.getArgs()));
        } catch (IOException ex) {
            respond(ResponseLevel.LEVEL_3, "Error getting player's UUID: `java.io.IOException`", e).queue();
            return;
        }
        try {
            JsonObject json = (new JsonParser()).parse(s).getAsJsonObject();
            EmbedBuilder eb = new EmbedBuilder();
            UUID uuid = UUIDFetcher.getUUID(json.get("id").getAsString());
            String separator = st.getConfig().getSeparator();
            if (st.getTimeDB().getConfig().getKeys().contains(uuid.toString())) {
                if (st.getSessionManager().contains(uuid)) {
                    eb.setDescription("Connection information for " + e.getArgs() + "\n**`In Session`**");
                    eb.addField("Current Session Information", consSessionDetail(st.getSessionManager().getSession(uuid)) + "\n\n", false);
                    eb.addField(separator, "", false);
                    eb = consConnectionDetail(eb, uuid);
                    respond(embedMessage(e, eb.build(), null), e).queue();
                } else {
                    eb.setDescription("Connection information for " + e.getArgs() + "\n**`Not In Session`**");
                    DateTime dt = new DateTime(st.getTimeDB().getLastLogOut(uuid));
                    TimeStamp ts = new TimeStamp(dt, st.getConfig().getTimeFormat());
                    eb.addField("Last Logout", "TimeStamp: " + ts.getPrettyDate(), false);
                    eb.addField(separator, "", false);
                    eb = consConnectionDetail(eb, uuid);
                    respond(embedMessage(e, eb.build(), null), e).queue();
                }
            } else {
                eb.setDescription("There are no time data for " + e.getArgs());
                respond(embedMessage(e, eb.build(), null), e).queue();
            }
        } catch (IllegalStateException ex) {
            respond(ResponseLevel.LEVEL_3, "That player cannot be found", e).queue();
        } catch (Exception ex) {
            ex.printStackTrace();
            respond(ResponseLevel.LEVEL_3, "Could not process request. Is MojangAPI down for UUID lookup?", e).queue();
        }
    }

    private String consSessionDetail(Session s) {
        return "Login TimeStamp: " + s.getStartSession().getPrettyDate() + "\n" +
                "Session Length: " + TimeStampUtil.simpleFormatElapsed(TimeStampUtil.computeDiff(s.getLongElapsed()));
    }

    private EmbedBuilder consConnectionDetail(EmbedBuilder eb, UUID uuid) {
        long lOT = getSt().getTimeDB().getOT(uuid);
        long lMT = getSt().getTimeDB().getMT(uuid);
        long lWT = getSt().getTimeDB().getWT(uuid);
        long lDT = getSt().getTimeDB().getDT(uuid);
        if (getSt().getSessionManager().contains(uuid)) {
            long elapsed = getSt().getSessionManager().getSession(uuid).getLongElapsed();
            lOT = lOT + elapsed;
            lMT = lMT + elapsed;
            lWT = lWT + elapsed;
            lDT = lDT + elapsed;
        }
        String oT = TimeStampUtil.simpleFormatElapsed(TimeStampUtil.computeDiff(lOT));
        String mT = TimeStampUtil.simpleFormatElapsed(TimeStampUtil.computeDiff(lMT));
        String wT = TimeStampUtil.simpleFormatElapsed(TimeStampUtil.computeDiff(lWT));
        String dT = TimeStampUtil.simpleFormatElapsed(TimeStampUtil.computeDiff(lDT));
        dT = dT.isEmpty() ? "No time date for today" : dT;
        eb.addField("Total Time Since Epoch", "Time: " + "`" + oT + "`", true);
        eb.addField("Total Time This Month", "Time: " + "`" + mT + "`", true);
        eb.addBlankField(true);
        eb.addField("Total Time This Week", "Time: " + "`" + wT + "`", true);
        eb.addField("Total Time Today", "Time: " + "`" + dT + "`", true);
        eb.addBlankField(true);
        return eb;
    }
}
