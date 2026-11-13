package com.abhishek.tenanthub.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Rectangle point
 * </p>
 *
 * @author WangChenChen
 * @version 1.2
 * @date 2022/7/22 10:12
 */


@Data
public class RectPoint<T> {

    /**
     * x Coordinates
     */
    @Schema(description = "x Coordinates")
    private Double x;

    /**
     * y Coordinates
     */
    @Schema(description = "y Coordinates")
    private Double y;

    /**
     * Unique ID
     */
    private T id;

    public RectPoint() {
    }

    public RectPoint(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public RectPoint(Double x, Double y, T id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
}
