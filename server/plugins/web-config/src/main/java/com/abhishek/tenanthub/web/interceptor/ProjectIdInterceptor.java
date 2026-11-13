package com.abhishek.tenanthub.web.interceptor;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.util.ServletUtils;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.internal.api.ApiCmsProjectService;
import com.abhishek.tenanthub.internal.api.ApiSysTenantService;
import com.abhishek.tenanthub.internal.dto.CmsProjectDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.abhishek.tenanthub.auth.enums.OAuth2Error.NO_HAVE_PROJECT_PERMISSIONS;
import static com.abhishek.tenanthub.common.util.TenantUtils.X_PROJECT_ID;


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
public class ProjectIdInterceptor implements HandlerInterceptor {

    private final ApiCmsProjectService projectService;

    private final ApiSysTenantService tenantService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
         * From the request header, e.g.:
         * GET https://www.baidu.com/hello
         * x-project-id=10001
         * */
        var projectIdStr = request.getHeader(X_PROJECT_ID);
        /*
         * From the URL, e.g.:
         * GET https://www.baidu.com/hello?x-project-id=10001
         * */
        var paramProjectIdStr = request.getParameter(X_PROJECT_ID);
        // URL project ID in. Overrides the project in the request header id
        if (StrUtil.isEmpty(projectIdStr) || StrUtil.isNotEmpty(paramProjectIdStr)) {
            projectIdStr = paramProjectIdStr;
        }
        if (StrUtil.isNotEmpty(projectIdStr)) {
            var projectId = NumberUtil.parseLong(projectIdStr);
            TenantUtils.setProjectId(projectId);
        } else {
            // Use the default project ID
            var defaultProjectId = tenantService.getByMultiTenantProperties().getDefaultProjectId();
            TenantUtils.setProjectId(defaultProjectId);
        }
        // Check whether the current tenant has this project ID
        var flag = tenantService.existTenantIncludeProjectId(TenantUtils.getTenantId(), TenantUtils.getProjectId());
        if (!flag) {
            var st = tenantService.getSmallByTenantId(TenantUtils.getTenantId());
            var err = JsonResult.error(NO_HAVE_PROJECT_PERMISSIONS.getStatus(),
                    String.format("Tenant %s has no project with ID %d", st.getName(), TenantUtils.getProjectId())
            );
            ServletUtils.renderError(response, err);
            return false;
        }
        // Check whether the current user may access this project
        if (!haveProjectPermissions(response, TenantUtils.getProjectId())) {
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


    /**
     * Check whether the current user has permission for this project
     */
    private boolean haveProjectPermissions(HttpServletResponse response, Long projectId) {
        // If the current user is a non-admin tenant user.
        if (JwtSecurityUtils.isAuthenticated() && (!JwtSecurityUtils.isMasterUser() && !JwtSecurityUtils.isAdminUser())) {
            var haveProjectIds = JwtSecurityUtils.getLoginUser().getHaveProjectIds();
            if (!haveProjectIds.isEmpty() && !haveProjectIds.contains(projectId)) {
                var err = String.format("You do not have permission for project ID %d", projectId);
                var first = haveProjectIds.stream().findFirst();
                var result = JsonResult.error(NO_HAVE_PROJECT_PERMISSIONS.getStatus(), err, CmsProjectDTO.class);
                var firstProject = projectService.getSimpleById(first.get());
                result.setData(firstProject);
                ServletUtils.renderError(response, result);
                return false;
            }
        }
        return true;
    }


}

