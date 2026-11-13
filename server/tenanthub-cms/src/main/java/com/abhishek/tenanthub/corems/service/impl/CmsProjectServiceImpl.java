package com.abhishek.tenanthub.corems.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.abhishek.tenanthub.common.enums.StatusEnum;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.corems.entity.CmsProject;
import com.abhishek.tenanthub.corems.mapper.CmsProjectMapper;
import com.abhishek.tenanthub.corems.po.ProjectQueryPO;
import com.abhishek.tenanthub.corems.service.CmsProjectService;
import com.abhishek.tenanthub.corems.vo.ResetPasswordVO;
import com.abhishek.tenanthub.internal.api.ApiPmsDeptService;
import com.abhishek.tenanthub.internal.api.ApiSysConfigService;
import com.abhishek.tenanthub.internal.api.ApiSysTenantService;
import com.abhishek.tenanthub.tenant.base.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.encryptPassword;
import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.matchesPassword;
import static com.abhishek.tenanthub.common.consts.ConfigDataConst.DEFAULT_PROJECT_PASSWORD;
import static com.abhishek.tenanthub.common.consts.MbpConst.PROJECT_ID;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:36
 */


@Slf4j
@Service
@AllArgsConstructor
public class CmsProjectServiceImpl extends BaseServiceImpl<CmsProjectMapper, CmsProject> implements CmsProjectService {

    private final ApiPmsDeptService apiPmsDeptService;

    private final ApiSysConfigService configService;

    private final ApiSysTenantService tenantService;


    @Override
    public IPage<CmsProject> pageKeywords(ProjectQueryPO params) {
        var project = new CmsProject();
        if (StrUtil.isNotBlank(params.getKeywords())) {
            project.setProjectName(params.getKeywords());
        }
        if (params.getDeptId() != null && params.getDeptId() > 0L) {
            project.setDeptId(params.getDeptId());
        }
        Page<CmsProject> pageRequest = Page.of(params.getPageIndex(), params.getPageSize());
        var result = baseMapper.selectProjectPage(pageRequest, project);
        if (params.isIncludeCauu()) {
            reflectionFill(result.getRecords());
        }
        if (params.isIncludeDept()) {
            setDept(result.getRecords());
        }
        result.getRecords().forEach(p -> p.setPassword(null));
        return result;
    }


    private void setDept(Collection<CmsProject> list) {
        if (!list.isEmpty()) {
            var deptIds = list.stream().map(CmsProject::getDeptId).collect(Collectors.toSet());
            if (deptIds.isEmpty()) {
                return;
            }
            var mapDept = apiPmsDeptService.mapFullNameByDeptIds(deptIds);
            list.forEach(p -> {
                var dept = mapDept.getOrDefault(p.getDeptId(), null);
                p.setDept(dept);
            });
        }
    }


    @Override
    public boolean save(CmsProject entity) {
        if (entity.getDeptId() == null) {
            throw new BizException("Project department is required");
        }
        if (StrUtil.isEmpty(entity.getPassword())) {
            var password = Optional.ofNullable(configService.getValueByKey(DEFAULT_PROJECT_PASSWORD.getKey()))
                    .orElse(DEFAULT_PROJECT_PASSWORD.getValue());
            entity.setPassword(password);
        }
        entity.setPassword(encryptPassword(entity.getPassword()));
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.NORMAL.getCode());
        }
        if (entity.getSort() == null) {
            var sort = (super.count() + 1) * 10;
            entity.setSort(sort);
        }
        var flag = super.save(entity);
        tenantService.tenantAddProjectId(TenantUtils.getTenantId(), entity.getProjectId());
        return flag;
    }


    @Override
    public boolean updateById(CmsProject entity) {
        entity.setPassword(null);
        return super.updateById(entity);
    }


    @Override
    public boolean resetPassword(ResetPasswordVO pwd) {
        var project = baseMapper.selectById(pwd.getProjectId());
        if (project != null) {
            // Encrypt the new password
            String newPassword = encryptPassword(pwd.getNewPwd());
            // Edit
            var pmsUser = new CmsProject()
                    .setProjectId(pwd.getProjectId())
                    .setPassword(newPassword);
            return super.updateById(pmsUser);
        }
        throw new BizException("Project does not exist; cannot reset its password");
    }


    @Override
    public IPage<CmsProject> simplePageKeywords(SearchPO params) {
        var project = new CmsProject();
        if (StrUtil.isNotBlank(params.getKeywords())) {
            project.setProjectName(params.getKeywords());
        }
        project.setStatus(StatusEnum.NORMAL.getCode());
        Page<CmsProject> pageRequest = Page.of(params.getPageIndex(), params.getPageSize());
        return baseMapper.selectSimpleProjectPage(pageRequest, project);
    }


    @Override
    public boolean removeById(CmsProject entity) {
        if (entity.getProjectId() == null) {
            throw new BizException("Enter a project ID");
        }
        if (StrUtil.isEmpty(entity.getPassword())) {
            throw new BizException("Enter the project password");
        }
        var dbProject = super.getById(entity.getProjectId());
        if (dbProject == null) {
            throw new BizException(String.format("Project ID %d does not exist", entity.getProjectId()));
        }
        var flag1 = matchesPassword(entity.getPassword(), dbProject.getPassword());
        if (!flag1) {
            throw new BizException(String.format("Incorrect password for project %s", dbProject.getProjectName()));
        }
        var flag2 = super.removeById(dbProject.getProjectId());
        // Query all tables with project_id
        Set<String> tableNames = baseMapper.selectListTableNamesByIncludeColumn(PROJECT_ID);
        // Delete this project from all tables
        if (!tableNames.isEmpty()) {
            baseMapper.deleteByProjectId(tableNames, dbProject.getProjectId());
        }
        // Remove this project from the tenant
        tenantService.tenantDelProjectId(TenantUtils.getTenantId(), dbProject.getProjectId());
        return flag2;
    }


}
