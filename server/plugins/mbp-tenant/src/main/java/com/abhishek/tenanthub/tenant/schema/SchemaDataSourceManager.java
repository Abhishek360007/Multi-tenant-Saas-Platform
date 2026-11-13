package com.abhishek.tenanthub.tenant.schema;

import com.abhishek.tenanthub.common.util.JsonUtils;
import com.abhishek.tenanthub.tenant.DatabaseManager;
import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Multi-tenancy by schema
 * Uses the default connection pool and switches the tenant schema,
 * MySQL has no schemas, so databases are used instead
 *
 * @author WangChenChen
 * @date 2022/12/11 11:29
 */


@Slf4j
@Component
@AllArgsConstructor
public class SchemaDataSourceManager implements DatabaseManager {

    // Default tenant data source
    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final MultiTenantProperties multiTenantProperties;

    private final MultiTenantSpringLiquibase mtsl;


    @Override
    public MultiTenantProperties getMultiTenantProperties() {
        return multiTenantProperties;
    }


    @Override
    public DataSource getDefaultDataSource() {
        return dataSource;
    }


    @Override
    public DataSource getDataSource(String tenantId) {
        return getDefaultDataSource();
    }


    @Async
    @Override
    public void asyncUpdateTable(String tenantId, Consumer<Exception> consumer) {
        try {
            updateTable(Set.of(tenantId));
            consumer.accept(null);
        } catch (Exception ex) {
            consumer.accept(ex);
        }
    }


    /**
     * Create tables
     */
    @Override
    public synchronized void updateTable(String tenantId) throws Exception {
        updateTable(Set.of(tenantId));
    }


    /**
     * Create tables
     */
    @Override
    public synchronized void updateTable(Set<String> tenantIds) throws Exception {
        if (tenantIds == null || tenantIds.isEmpty()) {
            return;
        }
        log.info("update table tenantId: \n{}", JsonUtils.toJson(tenantIds));
        // Build the database name with the prefix, e.g.: tenanthub_google
        var dbNames = tenantIds.stream()
                .map(tenantId -> multiTenantProperties.getPrefix() + tenantId)
                .collect(Collectors.toList());
        // Create the database if it does not exist
        dbNames.forEach(dbName -> {
            var sql = String.format("CREATE DATABASE IF NOT EXISTS %s CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;", dbName);
            jdbcTemplate.execute(sql);
        });
        mtsl.setSchemas(dbNames);
        mtsl.afterPropertiesSet();
    }


    @Override
    public synchronized void deleteTable(String tenantId) {
        log.warn("tenantId: {} delete table ...", tenantId);
        var tenantDbName = multiTenantProperties.getPrefix() + tenantId;
        var sql = String.format("DROP DATABASE %s ;", tenantDbName);
        jdbcTemplate.execute(sql);
    }


}
