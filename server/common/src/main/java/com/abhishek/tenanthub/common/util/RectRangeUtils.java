package com.abhishek.tenanthub.common.util;

import com.abhishek.tenanthub.common.domain.RectPoint;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * <p>
 * Rectangle range check
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/20 9:11
 */

public class RectRangeUtils {

    /**
     * Points within range ID
     *
     * @author WangChenChen
     * @date 2022/5/11 16:10
     */
    public static <T> Set<T> isIn(List<RectPoint<T>> list, RectPoint<T> topLeft, RectPoint<T> bottomRight) {
        return list.isEmpty() ? Set.of() : list.stream()
                .filter(target -> isIn(target, topLeft, bottomRight))
                .map(RectPoint::getId)
                .collect(Collectors.toSet());
    }


    /**
     * $-------------------------
     * -                        -
     * -           +            -
     * -                        -
     * -                        -
     * -------------------------@
     * <p>
     *
     * @param target      (+) Target point
     * @param topLeft     ($) Top-left corner
     * @param bottomRight (@) Bottom-right corner
     * @return true inside the rectangle, false if outside
     */
    public static <T> boolean isIn(RectPoint<T> target, RectPoint<T> topLeft, RectPoint<T> bottomRight) {
        // If within the latitude range
        if (isInRange(target.getX(), topLeft.getX(), bottomRight.getX())) {
            if (topLeft.getY() * bottomRight.getY() > 0) {
                return isInRange(target.getY(), topLeft.getY(), bottomRight.getY());
            } else {
                if (Math.abs(topLeft.getY()) + Math.abs(bottomRight.getY()) < 180) {
                    return isInRange(target.getY(), topLeft.getY(), bottomRight.getY());
                } else {
                    double left = Math.max(topLeft.getY(), bottomRight.getY());
                    double right = Math.min(topLeft.getY(), bottomRight.getY());
                    return isInRange(target.getY(), left, 180) || isInRange(target.getY(), right, -180);
                }
            }
        } else {
            return false;
        }
    }


    /**
     * Check whether the point is within the latitude/longitude range
     *
     * @param point
     * @param left
     * @param right
     * @return boolean
     */
    public static boolean isInRange(double point, double left, double right) {
        return point >= Math.min(left, right) && point <= Math.max(left, right);
    }


}
