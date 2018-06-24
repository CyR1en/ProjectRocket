package com.rocket.rocketbot.entity;


import com.rocket.rocketbot.accountSync.AccountDataFormat;
import com.rocket.rocketbot.utils.FinderUtils;
import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedHashMap;

public class UnifiedUser {

    @Getter private ProxiedPlayer proxiedPlayer;
    @Getter private DUser dUser;
    private AccountDataFormat accountDataFormat;

    public UnifiedUser(ProxiedPlayer p) {
        this.proxiedPlayer = p;
        User user = FinderUtils.findUserInDatabase(p);
        this.dUser = user == null ? null : new DUser(user);
    }

    public UnifiedUser(ProxiedPlayer p, DUser dUser) {
        this.proxiedPlayer = p;
        this.dUser = dUser;
    }

    public void setMcbUser(DUser dUser) {
        this.dUser = dUser;
    }

    public boolean isSynced() {
        return dUser != null && proxiedPlayer != null;
    }

    public AccountDataFormat getAccountDataFormat() {
        accountDataFormat = new AccountDataFormat(this);
        return accountDataFormat;
    }

    public LinkedHashMap<String, Object> getDataAsMap() {
        accountDataFormat = new AccountDataFormat(this);
        return accountDataFormat.dataAsMap();
    }
}
