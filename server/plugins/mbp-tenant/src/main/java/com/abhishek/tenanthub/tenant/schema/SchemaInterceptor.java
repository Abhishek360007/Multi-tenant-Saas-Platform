package com.abhishek.tenanthub.tenant.schema;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.executor.statement.StatementHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.common.consts.MbpConst.TENANT_IGNORE_TABLES;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/14 12:40
 */


@Slf4j
@AllArgsConstructor
public class SchemaInterceptor implements InnerInterceptor {


    private final MultiTenantProperties multiTenantProperties;


    @Override
    public void beforePrepare(StatementHandler sh, Connection conn, Integer transactionTimeout) {
        var mpBoundSql = PluginUtils.mpBoundSql(sh.getBoundSql());
        // Get the table names from the current SQL statement
        var tableName = getTableListName(mpBoundSql.sql());
        // Check whether the table should use the shared database instead of the tenant database.
        if (ignoreTable(tableName)) {
            // Switch database
            switchSchema(conn, multiTenantProperties.getDefaultTenantId());
        } else {
            var tenantId = getCurrentTenantId();
            log.debug("beforePrepare.tenantId: {}", tenantId);
            // Switch database
            switchSchema(conn, tenantId);
        }
        InnerInterceptor.super.beforePrepare(sh, conn, transactionTimeout);
    }


    private String getCurrentTenantId() {
        // Default tenant
        var defaultTenantId = multiTenantProperties.getDefaultTenantId();
        // 1.Check whether the request is signed in. 2. Check whether a custom tenant is used Id
        if (JwtSecurityUtils.isAuthenticated() && !TenantUtils.getUseCustomTenantId()) {
            // Determine the signed-in user type.
            // Platform users: can access any tenant database.
            // Tenant users: can only access their own tenant database
            if (JwtSecurityUtils.isMasterUser()) {
                return TenantUtils.getTenantId();
            } else {
                return JwtSecurityUtils.getTenantId();
            }
        }
        return Optional.ofNullable(TenantUtils.getTenantId())
                .orElse(defaultTenantId);
    }


    void switchSchema(Connection conn, String schema) {
        // Switch database with Connection.setCatalog rather than executing "USE ...":
        //  - HikariCP tracks a catalog changed through setCatalog and resets it to the pool's
        //    catalog (spring.datasource.hikari.catalog) when the connection goes back to the
        //    pool, so the next borrower never inherits this tenant's database. A raw USE
        //    statement is invisible to the pool and stayed on the connection.
        //  - The driver quotes the name itself; no SQL is built from the tenant ID.
        //  - It is skipped when the connection is already on that database.
        var database = multiTenantProperties.getPrefix() + schema;
        try {
            if (!database.equals(conn.getCatalog())) {
                conn.setCatalog(database);
            }
        } catch (SQLException e) {
            log.error("switchSchema: \n{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private final static TablesNamesFinder<Statement> TABLES_NAMES_FINDER = new TablesNamesFinder<>();


    /**
     * Parse the SQL to get all table names
     */
    private static Set<String> getTableListName(String sql) {
        Statements statements = null;
        try {
            statements = CCJSqlParserUtil.parseStatements(sql);
        } catch (JSQLParserException e) {
            log.error("getTableListName: \n{}", e.getMessage());
            return Set.of();
        }
        return statements.stream()
                .map(TABLES_NAMES_FINDER::getTables)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }


    /**
     * Skip shared tables.
     */
    private static boolean ignoreTable(Set<String> tableName) {
        return CollectionUtil.containsAny(TENANT_IGNORE_TABLES, tableName);
    }


}
