package com.abhishek.tenanthub.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 * Basic tenant details
 * </p>
 *
 * @author WangChenChen
 * @version 1.2
 * @date 2022/7/22 10:12
 */


@Schema(description = "Tenant")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SmallTenant implements java.io.Serializable {

    /**
     * Tenant ID
     */
    @Schema(description = "Tenant ID")
    private String tenantId;

    /**
     * Tenant logo
     */
    @Schema(description = "Tenant logo")
    private String logo;

    /**
     * Tenant name
     */
    @Schema(description = "Tenant name")
    private String name;

    /**
     * Contact name
     */
    @Schema(description = "Contact name")
    private String linkman;

    /**
     * Project IDs
     */
    @Schema(description = "Project IDs")
    private Set<Long> projectIds;

}
