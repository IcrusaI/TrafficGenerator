package com.crusa.trafficgenerator;

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

}
