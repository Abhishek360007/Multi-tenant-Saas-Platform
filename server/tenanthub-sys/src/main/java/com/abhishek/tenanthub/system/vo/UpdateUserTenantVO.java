package com.abhishek.tenanthub.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 * Link platform user to tenants
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
@Schema(description = "Grant a platform user access to several tenants")
public class UpdateUserTenantVO {

    @Schema(description = "User ID is required")
    @NotNull(message = "User ID is required!")
    private Long userId;

    private Set<String> tenantIds;

}
