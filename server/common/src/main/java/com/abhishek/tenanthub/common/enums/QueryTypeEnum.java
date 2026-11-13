package com.abhishek.tenanthub.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * Query type
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2023/12/5 9:31
 */

@Getter
@ToString
public enum QueryTypeEnum {

    YEAR("year", "Year"),

    QUARTER("quarter", "Quarter"),

    MONTH("month", "Month"),

    WEEK("week", "Week"),

    DAY("date", "Day");

    QueryTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    private final String code;

    private final String description;

    @JsonCreator
    public static QueryTypeEnum create(String value) {
        for (var v : values()) {
            if (v.code.equals(value)) {
                return v;
            }
        }
        return DAY;
    }

}
