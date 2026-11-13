package com.abhishek.tenanthub.common.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * <p>
 * Basic parameter passing
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @date 2021/10/21 21:38
 */

@Getter
@Setter
@ToString
@Schema(description = "Paged query")
public class PagePO implements java.io.Serializable {

    public PagePO() {
        this.pageIndex = 1;
        this.pageSize = 10;
    }

    /**
     * Current page
     */
    @Schema(description = "Current page", requiredMode = REQUIRED)
    private Integer pageIndex;

    /**
     * Rows per page
     */
    @Schema(description = "Items per page", requiredMode = REQUIRED)
    private Integer pageSize;

    /**
     * Start date
     */
    @Schema(description = "Start date")
    private LocalDate startDate;

    /**
     * End date
     */
    @Schema(description = "End date")
    private LocalDate endDate;

    /**
     * Sort direction "desc" or "asc".
     */
    @Schema(description = "Sort direction, true: ascending, false: descending")
    private boolean asc;

    /**
     * Field to sort by, e.g.: createUser,lastUpdateTime
     */
    @Schema(description = "Field to sort by, e.g.: createUser,lastUpdateTime")
    private Set<String> orderByColumns;

}
