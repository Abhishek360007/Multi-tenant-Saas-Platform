package com.abhishek.tenanthub.system.service;

import com.abhishek.tenanthub.system.vo.UserListTenantVO;

import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:33
 */

public interface SysUserService {


    /**
     * Get tenants linked to a platform user ID
     *
     * @param userId
     * @param tenantIds
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    Set<String> listHaveTenantIds(Long userId);


    /**
     * Get tenants linked to a platform user ID
     *
     * @param userId
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    UserListTenantVO listHaveTenants(Long userId);


    /**
     * Update the tenants linked to a platform user
     *
     * @param userId
     * @param tenantIds
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    boolean updateTenant(Long userId, Set<String> tenantIds);


    /**
     * Remove platform users linked to the tenant
     *
     * @param tenantId
     * @author Wang Chen Chen
     * @date 2023/11/23 17:31
     */
    boolean deleteHaveSysUser(String tenantId);

}
