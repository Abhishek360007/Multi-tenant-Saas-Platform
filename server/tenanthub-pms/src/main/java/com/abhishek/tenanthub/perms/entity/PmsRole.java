package com.abhishek.tenanthub.perms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.ParamBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 * Role
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */

@Schema(description = "Role")
@TableName("pms_role")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PmsRole extends ParamBaseEntity {

    /**
     * Role ID
     */
    @Schema(description = "Role ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Role ID is required", groups = {ValidUpdate.class})
    private Long roleId;

    /**
     * Role name
     */
    @Schema(description = "Role name")
    @NotBlank(message = "Role name is required", groups = {ValidCreate.class})
    private String roleName;

    /**
     * Sort order
     */
    @Schema(description = "Sort order")
    @NotNull(message = "Sort order is required", groups = {ValidCreate.class})
    private Long sort;

    /**
     * Role description
     */
    @Schema(description = "Role description")
    @NotBlank(message = "Role description is required", groups = {ValidCreate.class})
    private String description;

    /**
     * Data scope (1: all data, 2: custom, 3: own department only, 4: own department and below, 5: own data only)
     */
    @Schema(description = "Data scope", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Data scope is required", groups = {ValidCreate.class})
    private Integer dataScope;

    /**
     * Custom data permissions
     */
    @Schema(description = "Custom data permissions")
    @TableField(exist = false)
    private Set<Long> deptIds;

}
