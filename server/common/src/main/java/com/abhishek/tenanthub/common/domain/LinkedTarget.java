package com.abhishek.tenanthub.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkedTarget implements Serializable {

    @Schema(description = "Target Id")
    private Long targetId;

    @Schema(description = "Source Id")
    private Long sourceId;

}
