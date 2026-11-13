package com.abhishek.tenanthub.corems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.corems.entity.CmsProject;
import com.abhishek.tenanthub.corems.entity.TenantAndProject;
import com.abhishek.tenanthub.tenant.ds.DataScope;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * <p>
 * Project
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/14 10:03
 */


public interface CmsProjectMapper extends BaseMapper<CmsProject> {


    // Query all tables without project_id
    @InterceptorIgnore(tenantLine = "true")
    Set<String> selectListTableNamesByNotIncludeColumn(String column);


    // Query all tables with project_id
    @InterceptorIgnore(tenantLine = "true")
    Set<String> selectListTableNamesByIncludeColumn(String column);


    // Delete project
    @InterceptorIgnore(tenantLine = "true")
    int deleteByProjectId(@Param("tableName") Set<String> tableName, @Param("projectId") Long projectId);


    // Query projects by tenant database name, e.g. tenanthub_master,tenanthub_google
    Set<TenantAndProject> selectListByTenantDbName(Collection<String> tenantDbNameList);


    /**
     * Paged query by conditions
     */
    @DataScope()
    List<CmsProject> selectProjectList(@Param("p") CmsProject user);


    /**
     * Paged query by conditions
     */
    @DataScope()
    IPage<CmsProject> selectProjectPage(IPage<?> page, @Param("p") CmsProject user);


    /**
     * Paged query by conditions
     */
    @DataScope()
    IPage<CmsProject> selectSimpleProjectPage(IPage<?> page, @Param("p") CmsProject user);

}
