package com.abhishek.tenanthub.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 * Update tenant templates
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 12:14
 */

@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "Tenant templates")
public class UpdateTenantTemplateIdVO implements java.io.Serializable {

    @Schema(description = "Tenant ID is required")
    @NotEmpty(message = "Tenant ID is required!")
    private String tenantId;

    @Schema(description = "Select at least one tenant permission template")
    @NotNull(message = "Tenant permission template is required")
    @Size(min = 1, message = "Select at least one tenant permission template")
    private Set<Long> templateIds;


}
