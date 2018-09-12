package com.rocket.rocketbot.commands.discordCommands;

import com.google.common.collect.Lists;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rocket.rocketbot.Bot;
import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.commands.DCommand;
import com.rocket.rocketbot.entity.CustomPrintStream;
import com.rocket.rocketbot.entity.Messenger;
import lombok.Getter;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import net.md_5.bungee.api.ProxyServer;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TransferData extends DCommand {

    private final Path transferablePath = Paths.get("plugins/RocketBot/transferable");

    public TransferData(RocketBot rocketBot) {
        super(rocketBot);
        this.name = "transfer";
        this.help = "Transfer data from old accounts.json or any json that follows the data format.";
        this.arguments = "<File name>";
        this.aliases = new String[]{"trans"};
        this.category = Bot.Categories.OWNER.getCategory();
        this.type = Type.EMBED;
        this.hidden = true;
        setupDataFolder();
    }

    @Override
    protected void doCommand(CommandEvent e) {
        ProxyServer.getInstance().getScheduler().runAsync(rocketBot, () -> {
            try {
                Path oldPath = Paths.get(transferablePath + "/" + e.getArgs());
                if (!Files.exists(oldPath)) {
                    e.reply(Messenger.embedMessage(e, String.format("File %s cannot be found!", e.getArgs()), Messenger.ResponseLevel.ERROR));
                    return;
                }
                JSONObject data = new JSONObject(new String(Files.readAllBytes(oldPath), StandardCharsets.UTF_8));
                List<String> keys = Lists.newArrayList(data.keys());
                String message = String.format("Transferring data from %s", e.getArgs());
                e.getTextChannel().sendMessage(Messenger.embedMessage(e, message, Messenger.ResponseLevel.INFO)).queue(m -> {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    CustomPrintStream ps = new CustomPrintStream(os, e, m);
                    ProgressBarBuilder pbb = new ProgressBarBuilder()
                            .setTaskName("Data Transfer")
                            .setInitialMax(keys.size())
                            .setUpdateIntervalMillis(900)
                            .setStyle(ProgressBarStyle.ASCII)
                            .setPrintStream(ps);
                    try (ProgressBar pb = pbb.build()) {
                        for (String k : keys) {
                            JSONObject user = data.getJSONObject(k);
                            rocketBot.getDb().insert(k,
                                    user.getString(DataKey.DISCORD_ID.toString()),
                                    user.getString(DataKey.DISCORD_USERNAME.toString()),
                                    user.getString(DataKey.MC_GROUP.toString()),
                                    user.getBoolean(DataKey.REWARDED.toString()));
                            pb.step();
                            if(pb.getCurrent() < keys.size())
                                pb.setExtraMessage("Transferring...");
                            else if (pb.getCurrent() >= keys.size())
                                pb.setExtraMessage("Transfer Complete");
                        }
                    }
                });

                /*
                for(String key : ProgressBar.wrap(keys, "Transferring")) {
                    JSONObject user = data.getJSONObject(key);
                    rocketBot.getDb().insert(key,
                            user.getString(DataKey.DISCORD_ID.toString()),
                            user.getString(DataKey.DISCORD_USERNAME.toString()),
                            user.getString(DataKey.MC_GROUP.toString()),
                            user.getBoolean(DataKey.REWARDED.toString()));
                }
                */
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void setupDataFolder() {
        boolean dirExist = Files.exists(transferablePath);
        if (!dirExist) {
            try {
                Files.createDirectories(transferablePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private enum DataKey {
        MC_GROUP("Minecraft-group"),
        DISCORD_ID("Discord-ID"),
        DISCORD_USERNAME("Discord-Username"),
        REWARDED("Rewarded");

        @Getter
        private String key;

        DataKey(String s) {
            this.key = s;
        }

        @Override
        public String toString() {
            return getKey();
        }
    }
}
