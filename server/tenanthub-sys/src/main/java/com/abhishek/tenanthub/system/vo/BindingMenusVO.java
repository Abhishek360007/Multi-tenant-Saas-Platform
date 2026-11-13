package com.abhishek.tenanthub.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * <p>
 * Menus linked to roles, departments, tenant templates, etc.
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/23 15:14
 */

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Linked menus")
public class BindingMenusVO implements java.io.Serializable {

    @Schema(description = "ID is required", requiredMode = REQUIRED)
    @NotNull(message = "ID is required!")
    private Long id;

    @Schema(description = "Select at least one menu", requiredMode = REQUIRED)
    @NotNull(message = "Menus are required!")
    @Size(min = 1, message = "Select at least one menu!")
    private Set<Long> menus;

}
