package com.rocket.rocketbot.accountSync.Authentication;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {

    private HashMap<String, AuthSession> authSessions;

    public AuthManager() {
        authSessions = new HashMap<>();
    }

    public AuthSession getSession(String authToken) {
        return authSessions.get(authToken);
    }

    public void addSession(AuthSession authSession) {
        authSessions.put(authSession.getAuthToken().toString(), authSession);
    }

    public void removeSession(String authToken) {
        authSessions.remove(authToken);
    }

    public void clearSessions() {
        authSessions.clear();
    }

    public HashMap<String, AuthSession> getAuthSessions() {
        return authSessions;
    }

    public boolean inSession(ProxiedPlayer pp) {
        for (Map.Entry<String, AuthSession> v : authSessions.entrySet())
            if (pp.getUniqueId().equals(v.getValue().getMcAcc().getUniqueId()))
                return true;
        return false;
    }
}
