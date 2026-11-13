package com.abhishek.tenanthub.common.consts;

import com.abhishek.tenanthub.common.util.TenantUtils;

import java.util.Set;

/**
 * <p>
 * redis key Constants
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/16 15:15
 */

public class RedisKeyConst {

    /**
     * Keys that are not isolated per tenant. equals
     * See the description below TenantStringRedisSerializer.ignoreEqualsKeys
     *
     * @see com.abhishek.tenanthub.redis.TenantStringRedisSerializer.ignoreEqualsKeys
     */
    public final static Set<String> IGNORE_EQUALS_KEYS = Set.of(
            TenantUtils.X_TENANT_ID,
            ConfigDataConst.REDIS_CACHE_KEY
    );


    /**
     * Keys that are not isolated per tenant. startsWith
     * See the description below TenantStringRedisSerializer.ignoreStartsWithKeys
     *
     * @see com.abhishek.tenanthub.redis.TenantStringRedisSerializer.ignoreStartsWithKeys
     * <p>
     */
    public final static Set<String> IGNORE_STARTS_WITH_KEYS = Set.of(
            LoginConst.CAPTCHA_CODE_KEY,
            LoginConst.LOGIN_TOKEN_KEY,
            LoginConst.FORCED_OFFLINE_KEY
    );


}
