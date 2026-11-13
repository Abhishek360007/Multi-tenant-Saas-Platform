package com.abhishek.tenanthub.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * 0.Third-party app
 * 1.Internal app
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/5 9:31
 */

@Getter
@ToString
public enum ClientType {

    OTHER(0, "Third-party app"),

    SYSTEM(1, "Internal app");

    ClientType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    private final int code;

    private final String description;

    @JsonCreator
    public static ClientType get(Integer code) {
        if (OTHER.code == code) {
            return OTHER;
        } else if (SYSTEM.code == code) {
            return SYSTEM;
        } else {
            return OTHER;
        }
    }

}
