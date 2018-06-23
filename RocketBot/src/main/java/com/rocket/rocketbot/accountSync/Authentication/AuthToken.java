package com.rocket.rocketbot.accountSync.Authentication;

import com.rocket.rocketbot.RocketBot;
import com.rocket.rocketbot.accountSync.exceptions.IllegalConfirmKeyException;
import com.rocket.rocketbot.accountSync.exceptions.IllegalConfirmRequesterException;
import com.rocket.rocketbot.accountSync.exceptions.IllegalConfirmSessionIDException;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.RandomStringUtils;


public class AuthToken implements Comparable<AuthToken> {

    @Getter private ProxiedPlayer mcAcc;
    @Getter private String sessionID;

    @Getter @Setter private String token;

    public AuthToken(String sessionID, ProxiedPlayer mcAcc) {
        this.sessionID = sessionID;
        this.mcAcc = mcAcc;
        token = generateToken();
    }

    public AuthToken(ProxiedPlayer mcAcc, AuthSession authSession) {
        this.sessionID = authSession.getSessionID();
        this.mcAcc = mcAcc;
        token = generateToken();
    }

    public AuthToken(ProxiedPlayer mcAcc, String token) throws IllegalConfirmKeyException {
        AuthManager authManager = RocketBot.getInstance().getAuthManager();
        AuthSession authSession = authManager.getSession(token);
        if(authSession == null) {
            throw new IllegalConfirmKeyException();
        }
        this.mcAcc = mcAcc;
        this.token = token;
        this.sessionID = authSession.getSessionID();
    }

    private String generateToken() {
        token = RandomStringUtils.randomAlphanumeric(6);
        return token;
    }

    boolean authenticateToken(AuthToken token) throws IllegalConfirmRequesterException, IllegalConfirmKeyException, IllegalConfirmSessionIDException {
        if (!compareToken(token))
            return false;
        if(!this.toString().equals(token.toString())) {
            throw new IllegalConfirmKeyException();
        } else if (!this.getMcAcc().equals(token.getMcAcc())) {
            throw new IllegalConfirmRequesterException();
        } else if (!this.getSessionID().equals(token.getSessionID())) {
            throw new IllegalConfirmSessionIDException();
        }
        return true;
    }

    private boolean compareToken(AuthToken token) {
        return this.compareTo(token) > 0;
    }

    public String toString() {
        return token;
    }

    @Override
    public int compareTo(AuthToken o) {
        if (o != null && o.getToken().equals(this.getToken()))
            return 1;
        return -1;
    }
}
