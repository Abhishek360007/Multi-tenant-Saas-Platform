package com.abhishek.tenanthub.common.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ValueRange<T> implements java.io.Serializable {

    @Schema(description = "Start")
    private T begin;

    @Schema(description = "End")
    private T end;

}
