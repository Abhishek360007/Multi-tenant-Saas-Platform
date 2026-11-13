package com.abhishek.tenanthub.perms.po;

import com.abhishek.tenanthub.common.po.SearchParentPO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * <p>
 * Button permissions returned after sign-in
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


@Getter
@Setter
@ToString
@Schema(description = "Linked menus")
public class UserQueryPO extends SearchParentPO {

    /**
     * Department ID
     */
    @Schema(description = "Department ID")
    private Long deptId;

    /**
     * Includes linked roles and departments
     * include roles and departments
     */
    @Schema(description = "Includes linked roles and departments")
    private Boolean includeRad;

    public boolean isIncludeRad() {
        return this.getIncludeRad() != null && this.getIncludeRad();
    }

}
