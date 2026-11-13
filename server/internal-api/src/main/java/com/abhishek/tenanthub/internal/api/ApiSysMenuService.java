package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.internal.dto.SysMenuDTO;

import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiSysMenuService {

    /**
     * Get non-tenant menus
     * Includes platform menus and common menus
     *
     * @author WangChenChen
     * @date 2023/2/14 10:39
     */
    Set<String> listPermsByNonTenant();


    /**
     * Get non-tenant menus
     * Includes platform menus and common menus
     *
     * @author WangChenChen
     * @date 2023/2/14 10:39
     */
    Set<SysMenuDTO> listMenuByNonTenant();


    /**
     * Get the menus linked to a tenant
     *
     * @author WangChenChen
     * @date 2023/2/14 10:39
     */
    Set<String> listPermsByTenantId(String tenantId);


    /**
     * Get the menus linked to a tenant
     *
     * @author WangChenChen
     * @date 2023/2/14 10:39
     */
    Set<SysMenuDTO> listMenuByTenantId(String tenantId);


    /**
     * Get menu keys by menu ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:39
     */
    Set<String> listPermsByMenuIds(Set<Long> menuIds);


    /**
     * Get menu keys by menu ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:39
     */
    Set<SysMenuDTO> listMenuByMenuIds(Set<Long> menuIds);


}
