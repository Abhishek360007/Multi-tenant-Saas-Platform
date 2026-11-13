package com.abhishek.tenanthub.internal.api;

import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiSysUserService {


    /**
     * Get the tenant by user ID
     */
    Set<String> listHaveTenantIds(Long userId);


}
