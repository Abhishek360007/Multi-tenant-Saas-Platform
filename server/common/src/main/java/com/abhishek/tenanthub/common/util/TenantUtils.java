package com.abhishek.tenanthub.common.util;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/11/25 11:14
 */


public class TenantUtils {

    // http Tenant request header
    public final static String X_TENANT_ID = "x-tenant-id";

    // http Tenant default project request header
    public final static String X_PROJECT_ID = "x-project-id";


    private final static ThreadLocal<String> TENANT_ID_THREAD_LOCAL = new NamedInheritableThreadLocal<>("TENANT_ID_THREAD_LOCAL");

    // Tenant user. Uses a custom tenant ID.
    private final static ThreadLocal<Boolean> USE_CUSTOM_TENANT_ID_THREAD_LOCAL = new NamedInheritableThreadLocal<>("USE_CUSTOM_TENANT_ID_THREAD_LOCAL");

    private final static ThreadLocal<Long> PROJECT_ID_THREAD_LOCAL = new NamedInheritableThreadLocal<>("PROJECT_ID_THREAD_LOCAL");


    /**
     * Forgets the tenant ID, project ID and custom-tenant flag of the current thread.
     *
     * <p>Servlet threads are pooled and reused: a value left behind by one request would be
     * seen by the next request handled on the same thread (for example one that never goes
     * through {@code TenantIdInterceptor}). {@code TenantContextCleanupFilter} calls this
     * at the start and end of every request.</p>
     */
    public static void clear() {
        TENANT_ID_THREAD_LOCAL.remove();
        USE_CUSTOM_TENANT_ID_THREAD_LOCAL.remove();
        PROJECT_ID_THREAD_LOCAL.remove();
    }

    /**
     * Get the tenant ID
     */
    public static String getTenantId() {
        if (StrUtil.isNotBlank(TENANT_ID_THREAD_LOCAL.get())) {
            return TENANT_ID_THREAD_LOCAL.get();
        }
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return null;
        }
        var request = attributes.getRequest();
        var header = request.getHeader(X_TENANT_ID);
        if (StrUtil.isNotBlank(header)) {
            return header;
        }
        return request.getParameter(X_TENANT_ID);
    }


    /**
     * Set the tenant ID
     */
    public static void setTenantId(String tenantId) {
        if (StrUtil.isNotBlank(tenantId)) {
            TENANT_ID_THREAD_LOCAL.set(tenantId);
        } else {
            TENANT_ID_THREAD_LOCAL.remove();
        }
    }


    /**
     * Get the project ID
     */
    public static Long getProjectId() {
        if (PROJECT_ID_THREAD_LOCAL.get() != null) {
            return PROJECT_ID_THREAD_LOCAL.get();
        }
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return null;
        }
        var request = attributes.getRequest();
        // Read the project from the request header first id
        var projectIdStr = request.getHeader(X_PROJECT_ID);
        if (StrUtil.isEmpty(projectIdStr) || !NumberUtil.isNumber(projectIdStr)) {
            // Read the project from the request parameters id
            projectIdStr = request.getParameter(X_PROJECT_ID);
        }
        if (StrUtil.isNotBlank(projectIdStr) && NumberUtil.isNumber(projectIdStr)) {
            return NumberUtil.parseLong(projectIdStr);
        }
        return null;
    }


    /**
     * Set the project ID
     */
    public static void setProjectId(Long projectId) {
        if (projectId != null) {
            PROJECT_ID_THREAD_LOCAL.set(projectId);
        } else {
            PROJECT_ID_THREAD_LOCAL.remove();
        }
    }


    /**
     * Tenant user. Uses a custom tenant ID
     */
    public static boolean getUseCustomTenantId() {
        return Optional.ofNullable(USE_CUSTOM_TENANT_ID_THREAD_LOCAL.get())
                .orElse(false);
    }


    /**
     * Tenant user. Uses a custom tenant ID
     */
    public static void setUseCustomTenantId(Boolean flag) {
        if (flag != null) {
            USE_CUSTOM_TENANT_ID_THREAD_LOCAL.set(flag);
        } else {
            USE_CUSTOM_TENANT_ID_THREAD_LOCAL.remove();
        }
    }


}
