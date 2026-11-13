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
public enum UserType {

    TENANT((byte) 0, "Tenant user"),

    SYSTEM((byte) 1, "Platform user");

    UserType(byte code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static UserType get(byte code) {
        if (TENANT.code == code) {
            return TENANT;
        } else if (SYSTEM.code == code) {
            return SYSTEM;
        } else {
            return TENANT;
        }
    }

    @JsonValue
    private final byte code;

    private final String description;

}
