package com.crusa.trafficgenerator.distribution;

import com.crusa.trafficgenerator.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Экспоненциальное распределение — это непрерывное распределение, ограниченное снизу. Его форма всегда остается
 * неизменной: оно начинается с конечного значения при минимальном значении аргумента и непрерывно уменьшается при
 * увеличении x. С увеличением х скорость уменьшения экспоненциального распределения возрастает.
 */
public class ExponentialDistribution {

    /**
     * Используется при генерации.
     * Таблица, содержащая константы
     * q_i = sum_{j=1}^i (ln 2)^j/j! = ln 2 + (ln 2)^2/2 + ... + (ln 2)^i/i!
     * до тех пор, пока не будет превышена наибольшая представимая дробь ниже 1.
     *
     * Обратите внимание, что
     * 1 = 2 - 1 = exp(ln 2) - 1 = sum_{n=1}^infty (ln 2)^n / n!
     * Таким образом, q_i -> 1 при i -> +inf,
     * поэтому чем выше i, тем ближе к единице (ряд не чередующийся).
     *
     * n = 16 в Java достаточно, чтобы достичь 1,0.
     */
    private static final double[] EXPONENTIAL_SA_QI;

    /**
     * Инициализация таблицы
     */
    static {
        /**
         * Заполнение таблицы EXPONENTIAL_SA_QI. Мы не хотим, чтобы qi = 0 в таблице.
         */
        final double LN2 = Math.log(2);
        double qi = 0;
        int i = 1;

        /**
         * ArithmeticUtils предоставляет факториалы до 20, так что воспользуемся этим
         * ограничение вместе с Precision.EPSILON для создания следующего
         * код.(элементов будет 16)
         */
        List<Double> ra = new ArrayList<Double>();

        while (qi < 1) {
            qi += Math.pow(LN2, i) / Utils.getFactorial(i);
            ra.add(qi);
            ++i;
        }

        EXPONENTIAL_SA_QI = ra.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     *  Метод инверсии для генерации экспоненциально распределенных случайных значений.
     *
     * @param mean среднее значение этого распределения
     * @return сгенерированное значение
     */
    public static double exponential(double mean) {
        Random random = new Random();
        // Step 1:
        double a = 0; // начальное значение для экспоненциальной выборки
        double u = random.nextDouble(); // u имеет непрерывное равномерное распределение на интервале (0, 1)

        // Step 2 and 3: увеличиваем a пока u не будет больше 0.5
        while (u < 0.5) {
            a += EXPONENTIAL_SA_QI[0];
            u *= 2;
        }

        // Step 4 (сейчас u >= 0.5):
        u += u - 1;

        // Step 5: находим случайное значение, если u меньше или равно значению из экспоненциальной выборки
        if (u <= EXPONENTIAL_SA_QI[0]) {
            return mean * (a + u);
        }

        // Step 6: если не выполнилось предидущее условие, то находим минимальное значение u сравнивая его с значениями
        // из таблицы экспоненциальной выборки
        int i = 0; // Должно быть 1, если мы итерируем перед ним, используя 0
        double u2 = random.nextDouble();
        double umin = u2;

        // Step 7 and 8: находим минимальное значение
        do {
            ++i;
            u2 = random.nextDouble();

            if (u2 < umin) {
                umin = u2;
            }

            // Step 8:
        } while (u > EXPONENTIAL_SA_QI[i]); // Выход гарантирован, поскольку EXPONENTIAL_SA_QI[MAX] = 1

        // вычисляем случайное число
        return mean * (a + umin * EXPONENTIAL_SA_QI[0]);
    }
}