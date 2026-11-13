package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.common.domain.SmallTenant;
import com.abhishek.tenanthub.internal.dto.MultiTenantPropertiesDTO;
import com.abhishek.tenanthub.internal.dto.SysTenantDTO;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiSysTenantService {


    /**
     * Check whether a tenant ID exists
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    boolean existById(String tenantId);


    /**
     * Check whether the tenant contains the project ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    boolean existTenantIncludeProjectId(String tenantId, Long projectId);


    /**
     * Add a project to the tenant ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    void tenantAddProjectId(String tenantId, Long projectId);


    /**
     * Remove a project from the tenant ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    void tenantDelProjectId(String tenantId, Long projectId);


    /**
     * Get the tenant by tenant ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    SysTenantDTO getByTenantId(String tenantId);


    /**
     * Get the tenant by tenant ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    SmallTenant getSmallByTenantId(String tenantId);


    /**
     * Get the current tenant
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    SysTenantDTO getByCurrentTenant();


    /**
     * Get the default tenant ID
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    String getByDefaultTenantId();


    /**
     * Get the multi-tenant settings
     *
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    MultiTenantPropertiesDTO getByMultiTenantProperties();


}
