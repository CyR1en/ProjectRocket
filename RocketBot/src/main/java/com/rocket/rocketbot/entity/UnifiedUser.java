package com.rocket.rocketbot.entity;


import com.rocket.rocketbot.utils.Finder;
import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnifiedUser {

    @Getter private ProxiedPlayer proxiedPlayer;
    @Getter private DUser dUser;
    public UnifiedUser(ProxiedPlayer p) {
        this.proxiedPlayer = p;
        User user = Finder.findUserInDatabase(p);
        this.dUser = user == null ? null : new DUser(user);
    }

    public UnifiedUser(ProxiedPlayer p, DUser dUser) {
        this.proxiedPlayer = p;
        this.dUser = dUser;
    }

    public void setDUser(DUser dUser) {
        this.dUser = dUser;
    }

    public boolean isSynced() {
        return dUser != null && proxiedPlayer != null;
    }

}
