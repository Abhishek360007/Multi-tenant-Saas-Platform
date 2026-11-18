package com.abhishek.tenanthub.web;

import com.abhishek.tenanthub.common.util.TenantUtils;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextCleanupFilterTest {

    private final TenantContextCleanupFilter filter = new TenantContextCleanupFilter();

    @AfterEach
    void tidy() {
        TenantUtils.clear();
    }

    @Test
    void aTenantSetDuringARequestIsGoneAfterIt() throws Exception {
        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), (req, res) -> {
            TenantUtils.setTenantId("acme");
            TenantUtils.setProjectId(42L);
            TenantUtils.setUseCustomTenantId(true);
        });

        // No request bound to the thread now, so nothing may be left to read.
        assertNull(TenantUtils.getTenantId());
        assertNull(TenantUtils.getProjectId());
        assertFalse(TenantUtils.getUseCustomTenantId());
    }

    @Test
    void aValueLeftOnThePooledThreadIsNotSeenByTheNextRequest() throws Exception {
        TenantUtils.setTenantId("globex"); // left behind by some earlier code on this thread
        AtomicReference<String> seen = new AtomicReference<>("not called");

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(),
                (req, res) -> seen.set(TenantUtils.getTenantId()));

        assertNull(seen.get());
    }

    @Test
    void theContextIsClearedEvenWhenTheRequestFails() {
        assertThrows(ServletException.class, () -> filter.doFilter(new MockHttpServletRequest(),
                new MockHttpServletResponse(), (req, res) -> {
                    TenantUtils.setTenantId("acme");
                    throw new ServletException("boom");
                }));

        assertNull(TenantUtils.getTenantId());
    }
}
