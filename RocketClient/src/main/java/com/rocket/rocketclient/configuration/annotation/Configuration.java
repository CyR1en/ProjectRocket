package com.rocket.rocketclient.configuration.annotation;

import com.rocket.rocketclient.configuration.enums.Config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
    Config type();
    String[] header();
}
