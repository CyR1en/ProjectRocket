package com.rocket.rocketbot.accountSync.exceptions;

import com.rocket.rocketbot.RocketBot;

public class IllegalConfirmSessionIDException extends Exception {

    String msg;

    public IllegalConfirmSessionIDException() {
        msg = RocketBot.getLocale().getTranslatedMessage("exception.illegalconfirmsessionid").finish();
    }

    public IllegalConfirmSessionIDException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
