package com.rocket.rocketclient.configuration;

import java.util.List;

public interface IMCBConfig {

    String getString(Node node);

    boolean getBoolean(Node node);

    int getInt(Node node);

    List getList(Node node);
}
