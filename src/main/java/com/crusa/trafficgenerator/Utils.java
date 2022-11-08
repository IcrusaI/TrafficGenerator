package com.crusa.trafficgenerator;

import java.util.Random;

public class Utils {
    public static int getFactorial(int f) {
        if (f <= 1) {
            return 1;
        }
        else {
            return f * getFactorial(f - 1);
        }
    }

    public static final double RAND_MAX = 123456;

    /**
     * Базовый генератор, возвращающий псевдослучайное число, равномерно распределенное от 0 до некого RAND_MAX
     * @return псевдослучайное число
     */
    public static double basicRandGenerator() {
        Random random = new Random();
        double min = 0;
        return min + (RAND_MAX - min) * random.nextDouble();
    }
}
