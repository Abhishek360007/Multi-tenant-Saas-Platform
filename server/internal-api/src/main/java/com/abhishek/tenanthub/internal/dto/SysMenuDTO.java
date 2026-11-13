package com.abhishek.tenanthub.internal.dto;

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

@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysMenuDTO implements java.io.Serializable {

    /**
     * Menu ID
     */
    private Long menuId;

    /**
     * Parent menu
     */
    private Long parentId;

    /**
     * Menu name
     */
    private String menuName;

    /**
     * Permission key
     */
    private String perms;

    /**
     * Component
     */
    private String component;

    /**
     * Menu icon
     */
    private String icon;

    /**
     * Sort order
     */
    private Long sort;

    /**
     * Route path
     */
    private String path;

    /**
     * 0.Tenant user, 1 = platform user, 2 = all
     */
    private Byte target;

    /**
     * Menu type (0 = menu, 1 = button)
     */
    private Byte menuType;

    /**
     * Menu visibility (1 = shown, 0 = hidden)
     */
    private Byte visible;

    /**
     * Keep alive (0 = no, 1 = yes)
     */
    private Byte keepAlive;
}
