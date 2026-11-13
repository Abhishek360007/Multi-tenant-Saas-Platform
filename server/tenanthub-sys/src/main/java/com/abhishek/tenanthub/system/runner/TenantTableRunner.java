package com.abhishek.tenanthub.system.runner;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.abhishek.tenanthub.common.consts.MbpConst;
import com.abhishek.tenanthub.common.domain.SmallTenant;
import com.abhishek.tenanthub.common.util.JsonUtils;
import com.abhishek.tenanthub.internal.api.ApiCmsProjectService;
import com.abhishek.tenanthub.system.entity.SysTenant;
import com.abhishek.tenanthub.system.mapper.SysTenantMapper;
import com.abhishek.tenanthub.tenant.DatabaseManager;
import com.abhishek.tenanthub.tenant.service.MultiTenantManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * TODO Initialize the tenant tables
 * <p>
 *     Must run after
 *     @see com.abhishek.tenanthub.corems.runner.ProjectTableRunner.run()
 *     the method
 * </p>
 *
 * @author WangChenChen
 * @date 2022/12/7 21:20
 * <p>
 */


@Slf4j
@Component
@DependsOn({"projectTableRunner"})
@Order(Integer.MIN_VALUE + 1)
@AllArgsConstructor
public class TenantTableRunner {

    private final SysTenantMapper tenantMapper;

    private final DatabaseManager databaseManager;

    private final MultiTenantManager tenantManager;

    private final ApiCmsProjectService projectService;

    @PostConstruct
    public void init() {
        log.info("TenantTableRunner Initialized .....");
        var props = databaseManager.getMultiTenantProperties();
        if (props.getEnable()) {
            log.info("ignore tenant_id intercept table name : \n{}", JsonUtils.toJson(MbpConst.TENANT_IGNORE_TABLES));
            // Get the default tenant Id
            var defaultTenantId = props.getDefaultTenantId();
            var wrapper = new LambdaQueryWrapper<SysTenant>()
                    .select(List.of(SysTenant::getTenantId, SysTenant::getLogo,
                            SysTenant::getName, SysTenant::getLinkman));
            var smallTenantList = tenantMapper.selectList(wrapper)
                    .stream()
                    .map(source -> BeanUtil.copyProperties(source, SmallTenant.class))
                    .collect(Collectors.toSet());

            var tenantIds = smallTenantList.stream()
                    .map(SmallTenant::getTenantId)
                    .collect(Collectors.toSet());

            // Create tables
            if (props.getCreateTable()) {
                var newTenantIds = new HashSet<>(tenantIds);
                newTenantIds.remove(defaultTenantId);
                try {
                    databaseManager.updateTable(newTenantIds);
                } catch (Exception e) {
                    log.error("updateTable error: \n{}", e.getMessage());
                }
            }

            var tenantProjectMaps = projectService.mapByTenantDbName(props.getPrefix(), tenantIds);

            smallTenantList.forEach(t -> {
                t.setProjectIds(new HashSet<>());
                if (tenantProjectMaps.containsKey(t.getTenantId())) {
                    var projectIds = tenantProjectMaps.get(t.getTenantId());
                    t.getProjectIds().addAll(projectIds);
                }
            });

            tenantManager.addTenantIdBatch(smallTenantList);
        }
    }

    @PreDestroy
    public void destroy() {
        tenantManager.removeAll();
        log.info("delete the tenantId in redis ...");
    }

}
