package com.rocket.rocketbot.localization;


import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.configuration.SFile;

import java.util.logging.Level;

public class Locale extends SFile {

    public Locale(RocketBot rocketBot) {
        super(rocketBot, "Locale");
    }

    public Formatter getTranslatedMessage(String messagePath) {
        String translated = getConfig().getString(messagePath);
        if (translated == null || translated.isEmpty()) {
           getRocketBot().getLogger().log(Level.WARNING,"Can not get localization for " + messagePath + ". Returned path");
           return new Formatter(messagePath);
        }
        return new Formatter(translated);
    }

    public class Formatter {
        private String message;

        private Formatter(String message) {
            this.message = message;
        }

        public String f(Object... objects) {
            return String.format(message, objects);
        }

        public String finish() {
            return message;
        }
    }
}