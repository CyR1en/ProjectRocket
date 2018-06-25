package com.rocket.rocketclient.configuration;

public interface Node {

    Object getDefaultValue();

    String[] getComment();

    String key();

}
