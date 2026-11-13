package com.abhishek.tenanthub.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * Tenant data
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2023/11/07 10:42
 */

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TenantSimpleDataVO implements java.io.Serializable {

    @Schema(description = "Tenant ID")
    private String tenantId;

    @Schema(description = "Tenant name")
    private String name;

    @Schema(description = "Tenant logo")
    private String logo;

}
