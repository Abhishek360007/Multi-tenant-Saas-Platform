package com.abhishek.tenanthub.web.interceptor;


import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.domain.SmallTenant;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.util.ServletUtils;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.internal.api.ApiSysTenantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.abhishek.tenanthub.auth.enums.OAuth2Error.*;
import static com.abhishek.tenanthub.common.util.TenantUtils.X_TENANT_ID;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/11/15 11:41
 */


@Slf4j
@Component
@AllArgsConstructor
public class TenantIdInterceptor implements HandlerInterceptor {

    private final ApiSysTenantService tenantService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
         * From the request header, e.g.:
         * GET https://www.baidu.com/hello
         * x-tenant-id=master
         * */
        var tenantId = request.getHeader(X_TENANT_ID);
        /*
         * From the URL, e.g.:
         * GET https://www.baidu.com/hello?x-tenant-id=master
         * */
        var paramTenantId = request.getParameter(X_TENANT_ID);
        // URL tenant ID in. Overrides the tenant in the request header id
        if (StringUtils.isEmpty(tenantId) || StringUtils.isNotEmpty(paramTenantId)) {
            tenantId = paramTenantId;
        }
        if (StringUtils.isEmpty(tenantId)) {
            // Check whether the request is signed in.
            if (JwtSecurityUtils.isAuthenticated()) {
                // Determine the signed-in user type.
                // Platform users: must specify a tenant ID.
                // Tenant users: the tenant ID is fixed at sign-in
                if (JwtSecurityUtils.isMasterUser()) {
                    return writeError(response);
                } else {
                    tenantId = JwtSecurityUtils.getTenantId();
                }
            } else {
                return writeError(response);
            }
        }
        // Check that the tenant exists
        if (!tenantService.existById(tenantId)) {
            var err = String.format("Tenant ID %s does not exist", tenantId);
            ServletUtils.renderError(response, JsonResult.result(TENANT_ID_DOES_NOT_EXIST.getStatus(), err));
            return false;
        }
        TenantUtils.setTenantId(tenantId);
        // Check whether the current user may access this tenant
        if (!haveTenantPermissions(response, tenantId)) {
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


    /**
     * Check whether the current user has permission for this tenant
     */
    private boolean haveTenantPermissions(HttpServletResponse response, String tenantId) {
        // If the current user is a platform user
        if (JwtSecurityUtils.isMasterUser()) {
            var haveTenantIds = JwtSecurityUtils.getLoginUser().getHaveTenantIds();
            if (!haveTenantIds.isEmpty() && !haveTenantIds.contains(tenantId)) {
                var err = String.format("You do not have permission for tenant ID %s", tenantId);
                var first = haveTenantIds.stream().findFirst();
                var result = JsonResult.error(NO_HAVE_TENANT_PERMISSIONS.getStatus(), err, SmallTenant.class);
                var firstTenant = tenantService.getSmallByTenantId(first.get());
                result.setData(firstTenant);
                ServletUtils.renderError(response, result);
                return false;
            }
        }
        return true;
    }


    private static boolean writeError(HttpServletResponse response) {
        var err = String.format("Must be provided in the request header or URL parameters %s ", X_TENANT_ID);
        ServletUtils.renderError(response, JsonResult.result(REQUEST_MUST_ADD_TENANT_ID.getStatus(), err));
        return false;
    }

}

