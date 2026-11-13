package com.abhishek.tenanthub.perms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/9 17:46
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateUserRoleIdVO {


    @Schema(description = "User ID is required", requiredMode = REQUIRED)
    @NotNull(message = "User ID is required!")
    private Long userId;


    @Schema(description = "Role IDs", requiredMode = REQUIRED)
    @NotNull(message = "Role IDs!")
    @Size(min = 1, message = "Roles: select at least 1!")
    private Set<Long> roles;

}
