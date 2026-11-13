package com.abhishek.saas.common.consts;

import com.abhishek.saas.common.util.TenantUtils;

import java.util.Set;

/**
 * <p>
 * redis key 常量配置
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/16 15:15
 */

public class RedisKeyConst {

    /**
     * 不隔离的多租户的key。 equals
     * 描述请查看下面 TenantStringRedisSerializer.ignoreEqualsKeys
     *
     * @see com.abhishek.saas.redis.TenantStringRedisSerializer.ignoreEqualsKeys
     */
    public final static Set<String> IGNORE_EQUALS_KEYS = Set.of(
            TenantUtils.X_TENANT_ID,
            ConfigDataConst.REDIS_CACHE_KEY
    );


    /**
     * 不隔离的多租户的key。 startsWith
     * 描述请查看下面 TenantStringRedisSerializer.ignoreStartsWithKeys
     *
     * @see com.abhishek.saas.redis.TenantStringRedisSerializer.ignoreStartsWithKeys
     * <p>
     */
    public final static Set<String> IGNORE_STARTS_WITH_KEYS = Set.of(
            LoginConst.CAPTCHA_CODE_KEY,
            LoginConst.LOGIN_TOKEN_KEY,
            LoginConst.FORCED_OFFLINE_KEY
    );


}
