package com.abhishek.saas.corems.runner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.saas.common.consts.MbpConst;
import com.abhishek.saas.common.util.JsonUtils;
import com.abhishek.saas.corems.entity.CmsProject;
import com.abhishek.saas.corems.mapper.CmsProjectMapper;
import com.abhishek.saas.tenant.props.MultiTenantProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.abhishek.saas.common.consts.MbpConst.PROJECT_ID;


/**
 * 初始化 数据库中 包含 项目id 的 数据库表名称
 *
 * @author WangChenChen
 * @date 2022/12/7 21:20
 */


@Slf4j
@Component
@Order(Integer.MIN_VALUE)
@AllArgsConstructor
public class ProjectTableRunner {

    private final CmsProjectMapper projectMapper;

    private final MultiTenantProperties multiTenantProperties;

    @PostConstruct
    public void init() {
        log.info("ProjectTableRunner Initialized .....");
        if (multiTenantProperties.getEnableProject()) {
            // 查询 所有的 不包含 project_id 的表
            Set<String> tableNames = projectMapper.selectListTableNamesByNotIncludeColumn(PROJECT_ID);
            // 从 CmsProject 实体类中。获取 CmsProject 在 mysql 中的表名称。
            String projectTableName = CmsProject.class.getAnnotation(TableName.class).value();
            // 删除 CmsProject 的表名称
            tableNames.add(projectTableName);
            MbpConst.PROJECT_IGNORE_TABLES.addAll(tableNames);
            log.info("ignore project_id intercept table name : \n{}", JsonUtils.toJson(MbpConst.PROJECT_IGNORE_TABLES));
        }
    }

}
