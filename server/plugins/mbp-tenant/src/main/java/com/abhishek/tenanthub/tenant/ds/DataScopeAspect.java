package com.abhishek.tenanthub.tenant.ds;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.tenant.base.ParamBaseEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.abhishek.tenanthub.common.consts.DataScopeConst.*;

/**
 * Data filtering
 */

@Aspect
@Component
public class DataScopeAspect {


    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }


    protected void handleDataScope(final JoinPoint joinPoint, DataScope ds) {
        if (!JwtSecurityUtils.isAuthenticated()) {
            return;
        }
        // Data permissions are disabled for platform users and tenant admins
        if (JwtSecurityUtils.isAdminUser() || JwtSecurityUtils.isMasterUser()) {
            return;
        }
        // Get the current user
        var currentUser = JwtSecurityUtils.getLoginUser();
        dataScopeFilter(joinPoint, currentUser, ds.deptAlias(), ds.userAlias());
    }


    /**
     * Data scope filter
     *
     * @param joinPoint Pointcut
     * @param user      User
     * @param deptAlias Department alias
     * @param userAlias User alias
     */
    public static void dataScopeFilter(JoinPoint joinPoint, JwtLoginUser user, String deptAlias, String userAlias) {
        StringBuilder sqlString = new StringBuilder();
        var conditions = new ArrayList<Integer>();
        var scopeCustomIds = new ArrayList<String>();
        user.getRoles().forEach(role -> {
            if (Objects.equals(DATA_SCOPE_CUSTOM, role.getDataScope())) {
                scopeCustomIds.add(Convert.toStr(role.getRoleId()));
            }
        });
        for (var role : user.getRoles()) {
            var dataScope = role.getDataScope();
            if (conditions.contains(dataScope)) {
                continue;
            }
            if (Objects.equals(DATA_SCOPE_ALL, dataScope)) {
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                break;
            } else if (Objects.equals(DATA_SCOPE_CUSTOM, dataScope)) {
                if (scopeCustomIds.size() > 1) {
                    // Multiple custom data permissions use an IN query to avoid repeated joins.
                    sqlString.append(StrUtil.format(" OR {}.dept_id IN ( SELECT dept_id FROM pms_role_dept WHERE role_id in ({}) ) ", deptAlias, String.join(",", scopeCustomIds)));
                } else {
                    sqlString.append(StrUtil.format(" OR {}.dept_id IN ( SELECT dept_id FROM pms_role_dept WHERE role_id = {} ) ", deptAlias, role.getRoleId()));
                }
            } else if (Objects.equals(DATA_SCOPE_DEPT, dataScope)) {
                sqlString.append(StrUtil.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
            } else if (Objects.equals(DATA_SCOPE_DEPT_AND_CHILD, dataScope)) {
                sqlString.append(StrUtil.format(" OR {}.dept_id IN ( SELECT dept_id FROM pms_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )", deptAlias, user.getDeptId(), user.getDeptId()));
            } else if (Objects.equals(DATA_SCOPE_SELF, dataScope)) {
                if (StrUtil.isNotBlank(userAlias)) {
                    sqlString.append(StrUtil.format(" OR {}.user_id = {} ", userAlias, user.getUserId()));
                } else {
                    // Own-data scope without a userAlias returns no data
                    sqlString.append(StrUtil.format(" OR {}.dept_id = 0 ", deptAlias));
                }
            }
            conditions.add(dataScope);
        }
        // If no role has the given permission, sqlString is empty too, so return no data
        if (CollectionUtil.isEmpty(conditions)) {
            sqlString.append(StrUtil.format(" OR {}.dept_id = 0 ", deptAlias));
        }
        if (StrUtil.isNotBlank(sqlString.toString())) {
            var params = Arrays.stream(joinPoint.getArgs()).filter(a -> a instanceof ParamBaseEntity).findFirst();
            if (params.isPresent()) {
                var baseEntity = (ParamBaseEntity) params.get();
                baseEntity.getParamsValue().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }


    /**
     * Clear params.dataScope before building the permission SQL to prevent injection
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        var params = Arrays.stream(joinPoint.getArgs()).filter(a -> a instanceof ParamBaseEntity).findFirst();
        if (params.isPresent()) {
            var baseEntity = (ParamBaseEntity) params.get();
            baseEntity.getParamsValue().put(DATA_SCOPE, "");
        }
    }


}