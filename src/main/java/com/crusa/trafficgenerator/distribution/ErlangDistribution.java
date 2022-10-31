package com.crusa.trafficgenerator.distribution;

import java.util.Random;

/**
 * Распределение Эрланга является непрерывным распределением, ограниченным снизу. Оно представляет собой особый случай
 * Гамма распределения, где параметр m может принимать только положительные целые значения. По существу, у распределения
 * Эрланга нет области, в которой F(x) стремится к бесконечности при минимальном значении x [m<1], но при m=1 у него
 * есть особый случай, при котором оно сводится к экспоненциальному распределению.
 */
public class ErlangDistribution {

    /**
     * Генерирует значение согласно распределению Эрланга
     *
     * @param shape параметр формы shape >= 1
     * @param scale параметр масштаба
     * @return сгенерированное значение
     */
    public static double erlang(double shape, double scale)  {
        if (shape < 1 && (shape % 1) == 0) {
            throw new RuntimeException("Параметр shape может принимать только положительные целые значения");
        }

        Random random = new Random();

        // d= α - 1/3
        final double d = shape - 0.333333333333333333;
        final double c = 1 / (3 * Math.sqrt(d));

        while (true) {
            // Псевдослучайное число, распределенное по Гаусу
            final double x = random.nextGaussian();
            // Независимая случайная величина, равномерно распределённая на интервале (0, 1].
            // v = (1 + x * d)^3, при d= α - 1/3
            final double v = (1 + c * x) * (1 + c * x) * (1 + c * x);

            if (v <= 0) {
                continue;
            }

            final double x2 = x * x;
            final double u = random.nextDouble();

            // Ускоряем процедуру с помощью простого сжатия, которое позволяет избежать двух логарифмов
            // возвращаем scale * d * v, если  U < 1-0,0331 x^4
            if (u < 1 - 0.0331 * x2 * x2) {
                return scale * d * v;
            }

            //  генерируем нормальную переменную x и равномерную переменную U до тех пор,
            //  пока In(U)<0,5 x 2 + d - dv + d ln(v), затем возращаем scale * d * v
            if (Math.log(u) < 0.5 * x2 + d * (1 - v + Math.log(v))) {
                return scale * d * v;
            }
        }
    }
}
