package com.abhishek.tenanthub.tenant.service;


import com.abhishek.tenanthub.common.domain.SmallTenant;

import java.util.Set;

/**
 * TODO Multi-tenant manager
 * At startup, load every tenant in the database ID,
 * into Redis. Each request checks that the tenant ID exists,
 *
 * @author WangChenChen
 * @date 2022/12/11 9:45
 */

public interface MultiTenantManager {

    /**
     * TODO Get the default tenant
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    String getDefaultTenantId();


    /**
     * TODO Whether the current tenant is the default tenant ID
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    boolean isDefaultTenantId(String tenantId);


    /**
     * TODO Check whether a tenant exists by tenant ID
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    boolean existById(String tenantId);


    /**
     * TODO Get details by tenant ID
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    SmallTenant getById(String tenantId);


    /**
     * TODO Add tenant ID
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    void addTenantId(SmallTenant tenant);


    /**
     * TODO Add tenant ID
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    void addTenantIdBatch(Set<SmallTenant> tenants);


    /**
     * TODO Add tenant ID
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    void removeTenantId(String tenantId);


    /**
     * TODO Delete all tenants ID
     *
     * @author WangChenChen
     * @date 2022/12/11 9:46
     */
    void removeAll();

    /**
     * TODO Get the database name for a tenant ID
     */
    String getDbName(String tenantId);


}
