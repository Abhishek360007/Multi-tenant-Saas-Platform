package com.abhishek.tenanthub.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * 0. Tenant user
 * 1. Platform user
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/12 13:53
 */

@Getter
@ToString
public enum GenderType {

    FEMALE((byte) 0, "Female"),

    MALE((byte) 1, "Male"),

    UNKNOWN((byte) 2, "Unknown");

    GenderType(byte code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static GenderType get(byte code) {
        if (FEMALE.code == code) {
            return FEMALE;
        } else if (MALE.code == code) {
            return MALE;
        } else {
            return UNKNOWN;
        }
    }

    @JsonValue
    private final byte code;

    private final String description;

}
