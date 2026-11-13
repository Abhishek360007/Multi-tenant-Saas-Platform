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
@Schema(description = "Parent ID for paged search")
public class SearchParentPO extends SearchPO implements java.io.Serializable {

    @Schema(description = "Tree node parent ID")
    private Long parentId;

}
