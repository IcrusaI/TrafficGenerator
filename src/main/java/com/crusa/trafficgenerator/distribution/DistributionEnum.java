package com.crusa.trafficgenerator.distribution;

import java.util.Arrays;

public enum DistributionEnum {
    DELAY("delay"),
    ERLANG("erlang");


    private final String method;

    DistributionEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static String[] methods() {
        return Arrays.toString(DistributionEnum.values()).replaceAll("^.|.$", "").split(", ");
    }
}
