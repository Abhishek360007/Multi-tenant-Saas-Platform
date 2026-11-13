package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;


/**
 * <p>
 * Tenant permission template table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */

@Schema(description = "Tenant permission template")
@TableName("sys_template")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysTemplate extends BaseEntity {

    /**
     * Template ID
     */
    @Schema(description = "Template ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Template ID is required", groups = {ValidUpdate.class})
    private Long id;

    /**
     * Template name
     */
    @Schema(description = "Template name")
    @NotEmpty(message = "Template name is required", groups = {ValidCreate.class})
    private String name;

    /**
     * Template description
     */
    @Schema(description = "Template description")
    @NotEmpty(message = "Template description is required", groups = {ValidCreate.class})
    private String description;

    /**
     * Menu Id
     */
    @TableField(exist = false)
    @Schema(description = "Menu Id")
    private Set<Long> menuIds;

}
