package com.abhishek.tenanthub.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * Status [0 = disabled, 1 = normal, 2 = locked]
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/12 13:53
 */

@Getter
@ToString
public enum UserStatus {

    DISABLE(0, "Disabled"),

    LOCKING(2, "Locked"),

    NORMAL(1, "Normal");

    UserStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static UserStatus get(Integer code) {
        for (var v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        return DISABLE;
    }

    @JsonValue
    private final int code;

    private final String description;

}
