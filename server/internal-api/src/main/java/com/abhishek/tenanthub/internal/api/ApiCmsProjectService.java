package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.internal.dto.CmsProjectDTO;
import com.abhishek.tenanthub.internal.dto.SysTenantDTO;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * Project
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiCmsProjectService {

    /**
     * Number of projects in the current tenant
     */
    long countProjects();

    /**
     * Get basic project details by project ID, e.g. id, name,
     *
     * @author WangChenChen
     * @date 2023/8/11 10:47
     */
    CmsProjectDTO getSimpleById(Long projectId);


    /**
     * Initialize projects when a tenant is created
     *
     * @return
     * @author WangChenChen
     * @date 2023/2/14 10:53
     */
    void initProject(SysTenantDTO po);


    /**
     * Get the projects linked to a department ID
     *
     * @author WangChenChen
     * @date 2023/8/11 10:47
     */
    long countProjectByDeptId(Long deptId);


    /**
     * Get the projects linked to a department ID
     *
     * @author WangChenChen
     * @date 2023/8/11 10:47
     */
    Set<Long> listProjectByDeptId(Long deptId);


    /**
     * Get projects by tenant database prefix + tenant ID
     *
     * @date 2023/11/21 15:33
     */
    Map<String, Set<Long>> mapByTenantDbName(String dbNamePrefix, Collection<String> tenantIds);

}
