package com.rocket.rocketbot.utils;

import java.util.List;
import java.util.Random;

public class ListUtil {

    private final static Random random = new Random();

    public static <T> T chooseRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
