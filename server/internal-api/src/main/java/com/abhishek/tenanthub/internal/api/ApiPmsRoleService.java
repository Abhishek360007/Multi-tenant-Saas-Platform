package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.internal.dto.PmsRoleDTO;

import java.util.Set;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiPmsRoleService {


    /**
     * Get the roles linked to a user ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:47
     */
    Set<PmsRoleDTO> listByUserId(Long userId);


    /**
     * Get the menus linked to a role ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:47
     */
    Set<Long> listMenuIdByRoleIds(Set<Long> roleIds);


}
