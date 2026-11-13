package com.abhishek.tenanthub.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * Menu type
 * 0.Menu
 * 1.Button
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */

@Getter
@ToString
public enum MenuTypeEnum {

    MENU(0, "Menu"),

    BUTTON(1, "Button");

    MenuTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    private final int code;

    private final String description;


    /**
     * For Spring MVC entity parameter binding
     *
     * @author Wang Chen Chen
     * @date 2021/7/23 10:27
     */
    @JsonCreator
    public static MenuTypeEnum create(Integer value) {
        for (var v : values()) {
            if (v.code == value) {
                return v;
            }
        }
        return MENU;
    }

}
