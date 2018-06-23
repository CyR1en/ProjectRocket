package com.rocket.rocketbot.accountSync.exceptions;

import com.rocket.rocketbot.RocketBot;

public class IllegalConfirmRequesterException extends Exception{
    String msg;

    public IllegalConfirmRequesterException() {
        msg = RocketBot.getLocale().getTranslatedMessage("exception.illegalconfirmrequester").finish();
    }


    public IllegalConfirmRequesterException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
