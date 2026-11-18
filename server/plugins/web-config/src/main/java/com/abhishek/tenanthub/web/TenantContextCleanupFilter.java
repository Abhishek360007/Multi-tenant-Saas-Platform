package com.abhishek.tenanthub.web;

import com.abhishek.tenanthub.common.util.TenantUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Clears the per-thread tenant context ({@link TenantUtils}) at the start and end of
 * every request.
 *
 * <p>The tenant ID, project ID and custom-tenant flag live in {@code ThreadLocal}s. Tomcat
 * reuses its threads, so without this a value set for one request stayed on the thread and
 * could be read by a later request on it: one that skips {@code TenantIdInterceptor}, or
 * one where the interceptor stopped early (an interceptor's {@code afterCompletion} doesn't
 * run when its {@code preHandle} returns false, which is why this is a servlet filter with a
 * {@code finally} instead). It runs first, before Spring Security and the interceptors.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextCleanupFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        TenantUtils.clear(); // in case anything was left on this thread
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantUtils.clear();
        }
    }
}
