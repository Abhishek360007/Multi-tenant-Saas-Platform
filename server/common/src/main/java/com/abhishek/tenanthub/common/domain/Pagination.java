package com.abhishek.tenanthub.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 * Paged response
 * </p>
 *
 * @author WangChenChen
 * @version 1.0.1
 * @date 2021/10/14 14:00
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pagination<T> {

    /**
     * Total number of records
     */
    @Schema(description = "Total")
    private Long total;

    /**
     * Data
     */
    @Schema(description = "Data list")
    private List<T> list;

}

