package com.rocket.rocketbot.accountSync.exceptions;

import com.rocket.rocketbot.RocketBot;

public class IllegalConfirmKeyException extends Exception {
    String msg;

    public IllegalConfirmKeyException() {
        msg = RocketBot.getLocale().getTranslatedMessage("exception.illegalconfirmkey").finish();
    }

    public IllegalConfirmKeyException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
