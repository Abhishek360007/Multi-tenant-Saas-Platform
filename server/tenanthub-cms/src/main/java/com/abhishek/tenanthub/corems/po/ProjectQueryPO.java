package com.abhishek.tenanthub.corems.po;

import com.abhishek.tenanthub.common.po.SearchParentPO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


@Getter
@Setter
@ToString
@Schema(description = "Project query")
public class ProjectQueryPO extends SearchParentPO {

    /**
     * Department ID
     */
    @Schema(description = "Department ID")
    private Long deptId;

    /**
     * Includes linked department details
     * include departments
     */
    @Schema(description = "Includes linked department details")
    private Boolean includeDept;

    public boolean isIncludeDept() {
        return this.getIncludeDept() != null && this.getIncludeDept();
    }

}
