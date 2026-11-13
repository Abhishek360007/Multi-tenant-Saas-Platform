package com.abhishek.tenanthub.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/4 10:57
 */

public class ArithUtils {

    /**
     * Default division precision
     */
    private static final int DEF_DIV_SCALE = 10;

    /**
     * Exact addition.
     *
     * @param v1 Augend
     * @param v2 Addend
     * @return Sum of the two arguments
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * Exact subtraction.
     *
     * @param v1 Minuend
     * @param v2 Subtrahend
     * @return Difference of the two arguments
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * Exact multiplication.
     *
     * @param v1 Multiplicand
     * @param v2 Multiplier
     * @return Product of the two arguments
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * (Relatively) exact division; when not exact, rounded to
     * 10 decimal places, rounding half up.
     *
     * @param v1 Dividend
     * @param v2 Divisor
     * @return Quotient of the two arguments
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * (Relatively) exact division; when not exact, the scale argument
     * sets the precision, rounding half up.
     *
     * @param v1    Dividend
     * @param v2    Divisor
     * @param scale Number of decimal places to keep.
     * @return Quotient of the two arguments
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        if (b1.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.doubleValue();
        }
        return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Exact decimal rounding.
     *
     * @param v     Number to round
     * @param scale Decimal places to keep
     * @return Rounded result
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = BigDecimal.ONE;
        return b.divide(one, scale, RoundingMode.HALF_UP).doubleValue();
    }

}
