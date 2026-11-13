package com.abhishek.tenanthub.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.abhishek.tenanthub.common.consts.MbpConst.PROJECT_ID;
import static com.abhishek.tenanthub.common.consts.MbpConst.PROJECT_IGNORE_TABLES;


/**
 * <p>
 * Project ID interceptor
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/14 12:40
 */


@Slf4j
@Component
@AllArgsConstructor
public class ProjectLineHandler implements TenantLineHandler {

    private final MultiTenantProperties multiTenantProperties;


    @Override
    public Expression getTenantId() {
        var projectId = Optional.ofNullable(TenantUtils.getProjectId())
                .orElse(multiTenantProperties.getDefaultProjectId());
        return new LongValue(projectId);
    }


    /**
     * Get the project column name
     */
    @Override
    public String getTenantIdColumn() {
        return PROJECT_ID;
    }


    /**
     * Skip tables that do not need the project ID
     * Returns false by default: every table gets the condition
     */
    @Override
    public boolean ignoreTable(String tableName) {
        if (PROJECT_IGNORE_TABLES.isEmpty()) {
            return false;
        }
        return PROJECT_IGNORE_TABLES.stream().anyMatch(t -> t.equalsIgnoreCase(tableName));
    }


}
