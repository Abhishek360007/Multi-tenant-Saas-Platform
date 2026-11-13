package com.abhishek.tenanthub.common.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Schema(description = "Paged search")
public class SearchPO extends PagePO implements java.io.Serializable {

    /**
     * Search keyword
     */
    @Schema(description = "Keyword search")
    private String keywords;

    /**
     * Includes creator and last updater details
     * include create and update user
     */
    @Schema(description = "Includes creator and last updater details")
    private Boolean includeCauu;

    public boolean isIncludeCauu() {
        return this.getIncludeCauu() != null && this.getIncludeCauu();
    }

}
