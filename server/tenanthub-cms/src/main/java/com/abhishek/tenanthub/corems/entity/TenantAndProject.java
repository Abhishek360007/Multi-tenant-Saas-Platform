package com.abhishek.tenanthub.corems.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * <p>
 * Project table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Schema(description = "Project")
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TenantAndProject implements java.io.Serializable {

    /**
     * Project ID
     */
    @Schema(description = "Project ID")
    private Long projectId;

    /**
     * Tenant ID
     */
    @Schema(description = "Tenant ID")
    private String tenantId;

}
