package com.abhishek.tenanthub.tenant;

import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.tenant.service.MultiTenantManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>
 * 获取当前租户ID， 如果为空，那么就是默认租户
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/4/26 16:08
 */

@Component
@AllArgsConstructor
public class IndexNameGenerator {

    private final MultiTenantManager tenantManager;

    public String getTenantId() {
        return Optional.ofNullable(TenantUtils.getTenantId())
                .orElse(tenantManager.getDefaultTenantId());
    }

}