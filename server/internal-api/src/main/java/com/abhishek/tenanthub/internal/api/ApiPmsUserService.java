package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.internal.dto.InitUserDTO;
import com.abhishek.tenanthub.internal.dto.PmsUserDTO;

import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiPmsUserService {

    /**
     * Number of users in the current tenant
     */
    long countUsers();


    /**
     * Get the user by username
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    PmsUserDTO getByUsername(String username);


    /**
     * Get the user by mobile number
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    PmsUserDTO getByMobile(String mobile);


    /**
     * Get the user by email
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    PmsUserDTO getByEmail(String email);


    /**
     * Get the user by user ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    PmsUserDTO getByUserId(Long userId);


    /**
     * Get the user by username
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    Set<PmsUserDTO> listByUserIds(Set<Long> userIds);


    /**
     * Check whether a user ID exists
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    boolean existByUserId(Long userId);


    /**
     * Initialize users when a tenant is created
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    void initUserAndRoleAndDept(InitUserDTO po);

}
