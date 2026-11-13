package com.abhishek.tenanthub.corems.runner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.consts.MbpConst;
import com.abhishek.tenanthub.common.util.JsonUtils;
import com.abhishek.tenanthub.corems.entity.CmsProject;
import com.abhishek.tenanthub.corems.mapper.CmsProjectMapper;
import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.abhishek.tenanthub.common.consts.MbpConst.PROJECT_ID;


/**
 * Load the names of tables that have a project ID
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
            // Query all tables without project_id
            Set<String> tableNames = projectMapper.selectListTableNamesByNotIncludeColumn(PROJECT_ID);
            // Get the MySQL table name of the CmsProject entity.
            String projectTableName = CmsProject.class.getAnnotation(TableName.class).value();
            // Remove the CmsProject table name
            tableNames.add(projectTableName);
            MbpConst.PROJECT_IGNORE_TABLES.addAll(tableNames);
            log.info("ignore project_id intercept table name : \n{}", JsonUtils.toJson(MbpConst.PROJECT_IGNORE_TABLES));
        }
    }

}
