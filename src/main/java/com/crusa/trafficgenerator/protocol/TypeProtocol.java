package com.crusa.trafficgenerator.protocol;

import java.util.Arrays;

public enum TypeProtocol {
    TCP("TCP"),
    UDP("UDP");

    private final String name;

    TypeProtocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String[] names() {
        return Arrays.toString(TypeProtocol.values()).replaceAll("^.|.$", "").split(", ");
    }
}
