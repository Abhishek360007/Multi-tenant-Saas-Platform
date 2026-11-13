package com.abhishek.tenanthub.tenant.util;

import com.abhishek.tenanthub.common.util.TenantUtils;

import java.util.function.Supplier;


public class DelegateUtils {


    /**
     * Run this as another tenant
     * <p>
     * e.g. when adding a tenant, the tenant ID sent by the client is : master
     * After the tenant is added, its initial data must be inserted into the new tenant database.
     * Afterwards, restore the original tenant ID master
     *
     * @author WangChenChen
     * @date 2022/12/14 16:33
     */
    public static <S> S delegate(String targetTenantId, Supplier<S> fun) {
        // Get the original tenant ID
        var originalTenantId = TenantUtils.getTenantId();
        // Use a custom tenant ID
        TenantUtils.setUseCustomTenantId(true);
        // Set the target tenant ID
        TenantUtils.setTenantId(targetTenantId);
        // Call the target method
        var result = fun.get();
        // Set the original tenant ID
        TenantUtils.setTenantId(originalTenantId);
        // Stop using the custom tenant ID
        TenantUtils.setUseCustomTenantId(null);
        return result;
    }


}
