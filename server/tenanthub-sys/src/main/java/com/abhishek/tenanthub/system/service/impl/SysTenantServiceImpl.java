package com.abhishek.tenanthub.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.domain.SimpPushMessage;
import com.abhishek.tenanthub.common.domain.SmallTenant;
import com.abhishek.tenanthub.common.enums.StatusEnum;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.PasswordUtils;
import com.abhishek.tenanthub.common.util.IdUtils;
import com.abhishek.tenanthub.common.util.JsonUtils;
import com.abhishek.tenanthub.internal.api.ApiCmsProjectService;
import com.abhishek.tenanthub.internal.api.ApiPmsUserService;
import com.abhishek.tenanthub.internal.api.ApiSysConfigService;
import com.abhishek.tenanthub.internal.dto.InitUserDTO;
import com.abhishek.tenanthub.internal.dto.SysTenantDTO;
import com.abhishek.tenanthub.system.entity.SysTemplate;
import com.abhishek.tenanthub.system.entity.SysTenant;
import com.abhishek.tenanthub.system.mapper.SysTenantMapper;
import com.abhishek.tenanthub.system.po.CreateTenantPO;
import com.abhishek.tenanthub.system.po.TenantCreatedSuccessVO;
import com.abhishek.tenanthub.system.service.SysTemplateService;
import com.abhishek.tenanthub.system.service.SysTenantService;
import com.abhishek.tenanthub.system.service.SysUserService;
import com.abhishek.tenanthub.system.vo.TenantSimpleDataVO;
import com.abhishek.tenanthub.tenant.DatabaseManager;
import com.abhishek.tenanthub.tenant.base.service.impl.BaseServiceImpl;
import com.abhishek.tenanthub.tenant.service.MultiTenantManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.*;
import static com.abhishek.tenanthub.common.consts.ConfigDataConst.*;
import static com.abhishek.tenanthub.common.consts.SimpMessageConst.QUEUE_SINGLE_CREATE_TENANT;
import static com.abhishek.tenanthub.tenant.util.DelegateUtils.delegate;

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
public class SysTenantServiceImpl extends BaseServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {

    private final MultiTenantManager tenantManager;

    private final DatabaseManager dataSourceManager;

    private final ApiSysConfigService configService;

    private final SysTemplateService templateService;

    private final ApiPmsUserService userService;

    private final SysUserService sysUserService;

    private final ApiCmsProjectService projectService;

    private final SimpMessagingTemplate messagingTemplate;

    private final LiquibaseProperties liquibaseProperties;


    /**
     * uuid
     */
    public static String getUUIDSuffix() {
        return IdUtil.fastUUID().split(StrUtil.DASHED)[4];
    }


    @Override
    public IPage<SysTenant> pageKeywords(SearchPO params) {
        IPage<SysTenant> result = super.pageKeywords(
                params,
                List.of(SysTenant::getName, SysTenant::getLinkman, SysTenant::getAddress)
        );
        if (!result.getRecords().isEmpty()) {
            var collect = result.getRecords().stream()
                    .map(SysTenant::getTenantId)
                    .collect(Collectors.toSet());
            var templateMap = templateService.listByTenantIds(collect);
            result.getRecords().forEach(t -> {
                t.setTemplates(templateMap.getOrDefault(t.getTenantId(), new HashSet<>()));
            });
        }
        return result;
    }


    @Override
    public IPage<SysTenant> simplePageKeywords(SearchPO params) {
        var wrapper = super.getKeywordsQueryWrapper(
                        params,
                        List.of(SysTenant::getTenantId, SysTenant::getName, SysTenant::getLinkman)
                )
                .lambda()
                .select(List.of(SysTenant::getTenantId, SysTenant::getLogo, SysTenant::getName, SysTenant::getLinkman))
                .eq(SysTenant::getStatus, StatusEnum.NORMAL.getCode())
                .orderByAsc(SysTenant::getCreateTime);
        // If the signed-in user is linked to tenants,
        var tenantIds = sysUserService.listHaveTenantIds(getUserId());
        if (!tenantIds.isEmpty()) {
            wrapper.in(SysTenant::getTenantId, tenantIds);
        }
        Page<SysTenant> pageRequest = Page.of(params.getPageIndex(), params.getPageSize());
        return super.page(pageRequest, wrapper);
    }


    @Override
    public Collection<TenantSimpleDataVO> simpleSearchQuery(SearchPO po) {
        if (StringUtils.isBlank(po.getKeywords())) {
            return new ArrayList<>();
        }
        return delegate(tenantManager.getDefaultTenantId(), () -> {
            var wrapper = new LambdaQueryWrapper<SysTenant>()
                    .select(List.of(SysTenant::getTenantId, SysTenant::getLogo, SysTenant::getName))
                    .like(SysTenant::getTenantId, po.getKeywords())
                    .or()
                    .like(SysTenant::getName, po.getKeywords());
            Page<SysTenant> pageRequest = Page.of(po.getPageIndex(), po.getPageSize());
            return super.page(pageRequest, wrapper)
                    .getRecords()
                    .stream()
                    .map(t -> new TenantSimpleDataVO()
                            .setTenantId(t.getTenantId())
                            .setLogo(t.getLogo())
                            .setName(t.getName())
                    ).collect(Collectors.toSet());
        });
    }


    @Override
    public SysTenant getSimpleById(String id) {
        var wrapper = new LambdaQueryWrapper<SysTenant>()
                .select(
                        List.of(SysTenant::getTenantId, SysTenant::getLogo, SysTenant::getName)
                )
                .eq(SysTenant::getTenantId, id);
        return baseMapper.selectOne(wrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SysTenant entity) {
        if (!isMasterUser()) {
            throw new BizException("Only platform users can create tenants");
        }
        if (StringUtils.isBlank(entity.getTenantId())) {
            do {
                entity.setTenantId(getUUIDSuffix());
            }
            while (tenantManager.existById(entity.getTenantId()));
        } else {
            var isMatch = Pattern.matches("\\w{4,12}$", entity.getTenantId());
            if (!isMatch) {
                throw new BizException("Tenant ID must be 4 to 12 letters and numbers");
            }
            if (tenantManager.existById(entity.getTenantId())) {
                throw new BizException(String.format("Tenant ID %s already exists", entity.getTenantId()));
            }
        }
        if (entity.getExpired() == null) {
            entity.setExpired(LocalDateTime.now().plusYears(10));
        }
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.NORMAL.getCode());
        }
        if (entity.getLogo() == null) {
            var defaultLogoPath = Optional.ofNullable(configService.getValueByKey(DEFAULT_TENANT_LOGO.getKey()))
                    .orElse(DEFAULT_TENANT_LOGO.getValue());
            entity.setLogo(defaultLogoPath);
        }
        if (entity.getTemplates() != null && !entity.getTemplates().isEmpty()) {
            var templateIds = entity.getTemplates()
                    .stream()
                    .map(SysTemplate::getId)
                    .collect(Collectors.toSet());
            updateTemplate(entity.getTenantId(), templateIds);
        }
        return super.save(entity);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TenantCreatedSuccessVO create(CreateTenantPO po) throws Exception {
        if (!isMasterUser()) {
            throw new BizException("Only platform users can create tenants");
        }

        // If the tenant ID is empty, generate one
        if (StringUtils.isBlank(po.getTenantId())) {
            po.setTenantId(getUUIDSuffix());
        }

        // If the admin username is empty, generate one
        if (StringUtils.isBlank(po.getAdminUsername())) {
            po.setAdminUsername(getUUIDSuffix());
        }

        // Admin email; defaults to the tenant email if empty
        if (StringUtils.isBlank(po.getAdminEmail())) {
            po.setAdminEmail(po.getEmail());
        }

        // Admin name; defaults to the tenant name if empty
        if (StringUtils.isBlank(po.getAdminNickname())) {
            po.setAdminNickname(po.getName());
        }

        // Admin mobile number; defaults to the tenant contact number if empty
        if (StringUtils.isBlank(po.getAdminMobile())) {
            po.setAdminMobile(po.getContactNumber());
        }

        // No password given: generate a one-time random one (returned once below).
        // The admin is forced to change it at first login.
        if (StringUtils.isBlank(po.getAdminPwd())) {
            po.setAdminPwd(PasswordUtils.random());
        }

        var sysTenant = new SysTenant()
                .setTenantId(po.getTenantId())
                .setLogo(po.getLogo())
                .setName(po.getName())
                .setEmail(po.getEmail())
                .setLinkman(po.getLinkman())
                .setContactNumber(po.getContactNumber())
                .setAreaCode(po.getAreaCode())
                .setAddress(po.getAddress())
                .setExpired(po.getExpired())
                // Set the status to initializing...
                .setStatus((byte) 9);

        // Save the tenant
        this.save(sysTenant);

        // Add permission templates
        if (po.getTemplates() != null && !po.getTemplates().isEmpty()) {
            var tempIds = po.getTemplates().stream()
                    .map(SysTemplate::getId)
                    .collect(Collectors.toSet());
            var wrapper = new LambdaQueryWrapper<SysTemplate>()
                    .select(List.of(SysTemplate::getId))
                    .in(SysTemplate::getId, tempIds);
            var templateIds = templateService.list(wrapper)
                    .stream()
                    .map(SysTemplate::getId)
                    .collect(Collectors.toSet());
            baseMapper.insertByTemplates(sysTenant.getTenantId(), templateIds);
        }

        // Initialize users, roles and departments
        var initUserDTO = new InitUserDTO();
        BeanUtils.copyProperties(po, initUserDTO);

        // Initialize projects
        var initTenantDTO = new SysTenantDTO();
        BeanUtils.copyProperties(sysTenant, initTenantDTO);

        var loginUser = getLoginUser();

        // Initialize tenant data
        initData(loginUser, initTenantDTO, initUserDTO);

        return TenantCreatedSuccessVO.builder()
                .adminMobile(po.getAdminMobile())
                .adminNickname(po.getAdminNickname())
                .adminEmail(po.getAdminEmail())
                .adminUsername(po.getAdminUsername())
                .adminPwd(po.getAdminPwd())
                .build();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TenantCreatedSuccessVO resetData(String tenantId) {
        if (!isMasterUser() && !isAdminUser()) {
            throw new BizException("Only platform admins can reset tenant data");
        }
        var source = super.getById(tenantId);
        if (source == null) {
            throw new BizException(String.format("Tenant ID %s does not exist", tenantId));
        }
        var initTenantDTO = new SysTenantDTO();
        BeanUtils.copyProperties(source, initTenantDTO);

        // The reset recreates the tenant admin with a new username and a one-time random password
        var password = PasswordUtils.random();
        var initUser = new InitUserDTO()
                .setTenantId(source.getTenantId())
                .setName(source.getName())
                .setLogo(source.getLogo())
                .setAdminNickname(source.getName())
                .setAdminUsername(getUUIDSuffix())
                .setAdminMobile(source.getContactNumber())
                .setAdminEmail(source.getEmail())
                .setAdminPwd(password);

        var dbName = tenantManager.getDbName(tenantId);

        // Get all tables in the current tenant database
        var tableNames = delegate(tenantId, () -> baseMapper.selectListTableNames());

        // Exclude the Liquibase tracking tables. Defaults to DATABASECHANGELOGLOCK , DATABASECHANGELOG
        var excludeTableNames = List.of(liquibaseProperties.getDatabaseChangeLogLockTable(),
                liquibaseProperties.getDatabaseChangeLogLockTable().toLowerCase(),
                liquibaseProperties.getDatabaseChangeLogLockTable().toUpperCase(),
                liquibaseProperties.getDatabaseChangeLogTable(),
                liquibaseProperties.getDatabaseChangeLogTable().toLowerCase(),
                liquibaseProperties.getDatabaseChangeLogTable().toUpperCase());

        tableNames.removeAll(new HashSet<>(excludeTableNames));

        // Set the status to initializing...
        source.setStatus((byte) 9);
        super.updateById(source);

        var loginUser = getLoginUser();
        CompletableFuture.runAsync(() -> {
            // Clear all tables asynchronously
            delegate(tenantId, () -> baseMapper.truncateTableData(dbName, tableNames));
        }).whenComplete((val, ex) -> {
            if (ex == null) {
                // Recreate the schema and seed data
                delegate(tenantId, () -> {
                    initData(loginUser, initTenantDTO, initUser);
                    return true;
                });
            } else {
                log.error("truncateTableData error: {} ", ex.getMessage());
            }
        });
        return TenantCreatedSuccessVO.builder()
                .adminNickname(initUser.getAdminNickname())
                .adminUsername(initUser.getAdminUsername())
                .adminMobile(initUser.getAdminMobile())
                .adminEmail(initUser.getAdminEmail())
                .adminPwd(password)
                .build();
    }


    // Initialize the schema and default data
    private void initData(JwtLoginUser loginUser, SysTenantDTO initTenant, InitUserDTO initUser) {
        if (loginUser == null || initTenant == null || initUser == null) {
            return;
        }
        // Asynchronously initialize the schema and base data
        dataSourceManager.asyncUpdateTable(initTenant.getTenantId(),
                (Exception ex) -> {
                    // Set the signed-in user
                    JwtSecurityUtils.setLoginUser(loginUser);
                    var msg = new SimpPushMessage()
                            .setId(IdUtils.getStandaloneId())
                            .setTitle("Tenant initialization result")
                            .setStatus(StatusEnum.NORMAL.getCode())
                            .setCreateTime(LocalDateTime.now());
                    if (ex == null) {
                        // Initialize users, roles and departments
                        userService.initUserAndRoleAndDept(initUser);

                        // Initialize projects
                        projectService.initProject(initTenant);

                        var smallTenant = BeanUtil.copyProperties(initTenant, SmallTenant.class);
                        smallTenant.setProjectIds(new HashSet<>(Set.of(DEFAULT_PROJECT_ID.getValue())));
                        // Save the new tenant to Redis
                        tenantManager.addTenantId(smallTenant);

                        // Set the status to initialized
                        var t1 = new SysTenant()
                                .setTenantId(initTenant.getTenantId())
                                .setStatus(StatusEnum.NORMAL.getCode());

                        baseMapper.updateById(t1);
                        msg.setContent(String.format("Tenant %s initialized", initTenant.getName()));
                    } else {
                        log.error("tenantId: {} async update table : \n{}", initTenant.getTenantId(), ex.getMessage());
                        msg.setStatus(StatusEnum.DISABLE.getCode());
                        msg.setContent(String.format("Tenant %s failed to initialize: %s", initTenant.getName(), ex.getMessage()));
                    }
                    messagingTemplate.convertAndSendToUser(loginUser.getLoginId(), QUEUE_SINGLE_CREATE_TENANT, JsonUtils.toJson(msg));
                    // Clear the signed-in user
                    JwtSecurityUtils.setLoginUser(null);
                }
        );
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SysTenant entity) {
        if (!isMasterUser()) {
            throw new BizException("Only platform users can update tenants");
        }
        var templateIds = entity.getTemplates()
                .stream()
                .map(SysTemplate::getId)
                .collect(Collectors.toSet());
        updateTemplate(entity.getTenantId(), templateIds);
        return super.updateById(entity);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTemplate(String tenantId, Set<Long> templateIds) {
        if (!isMasterUser()) {
            throw new BizException("Only platform users can update tenant permissions");
        }
        // Delete the assigned templates first ID
        baseMapper.deleteHaveTemplates(tenantId);
        if (templateIds == null || templateIds.isEmpty()) {
            return true;
        }
        // Add permission templates to the tenant
        return baseMapper.insertByTemplates(tenantId, templateIds) > 0;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        if (!isMasterUser() && !isAdminUser()) {
            throw new BizException("Only platform admins can delete tenants");
        }
        var tenantId = String.valueOf(id);
        if (!this.exist(SysTenant::getTenantId, tenantId)) {
            return false;
        }
        if (StringUtils.equals(tenantId, tenantManager.getDefaultTenantId())) {
            throw new BizException(String.format("The default tenant %s cannot be deleted", tenantId));
        }
        // Delete the tenant
        var flag = super.removeById(tenantId);
        // Delete the tenant template permissions
        baseMapper.deleteHaveTemplates(tenantId);
        // Drop the tenant database
        dataSourceManager.deleteTable(tenantId);
        // Remove the tenant from Redis
        tenantManager.removeTenantId(tenantId);
        // Remove platform users linked to this tenant
        sysUserService.deleteHaveSysUser(tenantId);
        return flag;
    }


}
