package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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


/**
 * <p>
 * Menu table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */

@Schema(description = "Permission menu")
@TableName("sys_menu")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysMenu extends BaseEntity {

    /**
     * Menu ID
     */
    @Schema(description = "Menu ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Menu ID is required", groups = {ValidUpdate.class})
    private Long menuId;

    /**
     * Parent menu
     */
    @Schema(description = "Parent menu")
    @NotNull(message = "Parent menu is required", groups = {ValidCreate.class})
    private Long parentId;

    /**
     * Menu name
     */
    @Schema(description = "Menu name")
    @NotEmpty(message = "Menu name is required", groups = {ValidCreate.class})
    private String menuName;

    /**
     * Permission key
     */
    @Schema(description = "Permission key")
    @NotEmpty(message = "Permission key is required", groups = {ValidCreate.class})
    private String perms;

    /**
     * Component
     */
    @Schema(description = "Component")
    @NotEmpty(message = "Component is required", groups = {ValidCreate.class})
    private String component;

    /**
     * Menu icon
     */
    @Schema(description = "Menu icon")
    @NotEmpty(message = "Menu icon is required", groups = {ValidCreate.class})
    private String icon;

    /**
     * Sort order
     */
    @Schema(description = "Sort order")
    @NotNull(message = "Sort order is required", groups = {ValidCreate.class})
    private Long sort;

    /**
     * Route path
     */
    @Schema(description = "Route path")
    @NotEmpty(message = "Route path is required", groups = {ValidCreate.class})
    private String path;

    /**
     * 0.Tenant user, 1 = platform user, 2 = all
     */
    @Schema(description = "Target users: 0 = tenant users, 1 = platform users, 2 = all")
    @NotNull(message = "Target users is required", groups = {ValidCreate.class})
    private Byte target;

    /**
     * Menu type (0 = menu, 1 = button, 2 = external link)
     */
    @Schema(description = "Menu type (0 = menu, 1 = button, 2 = external link)")
    @NotNull(message = "Menu type is required", groups = {ValidCreate.class})
    private Byte menuType;

    /**
     * Menu visibility (1 = shown, 0 = hidden)
     */
    @Schema(description = "Menu visibility (1 = shown, 0 = hidden)")
    @NotNull(message = "Menu status is required", groups = {ValidCreate.class})
    private Byte visible;

    /**
     * Keep alive (1 = yes, 0 = no)
     */
    @Schema(description = "Keep alive (1 = yes, 0 = no)")
    @NotNull(message = "Keep-alive is required", groups = {ValidCreate.class})
    private Byte keepAlive;

}
