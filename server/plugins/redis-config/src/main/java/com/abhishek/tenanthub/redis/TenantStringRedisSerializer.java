package com.abhishek.tenanthub.redis;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.Set;
import java.util.function.Supplier;

/**
 * <p>
 * redis Custom serializer that prefixes keys with the tenant to isolate data
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @version 2.0
 * @date 2019/12/12 23:12
 */

@Slf4j
public class TenantStringRedisSerializer implements RedisSerializer<String> {

    private final Supplier<String> getTenantId;

    /**
     * Keys that are not isolated per tenant. equals
     * If the collection contains "captcha_codes" then
     * Every key except captcha_codes gets the tenant prefix ID
     */
    private final Set<String> ignoreEqualsKeys;

    /**
     * Keys that are not isolated per tenant. startsWith
     * If the collection contains "captcha_codes:" then
     * <p>
     * These Redis keys do not get the tenant ID:
     * key1 :  captcha_codes:123456
     * key2 :  captcha_codes:user:123456
     * <p>
     * These Redis keys are prefixed with the tenant ID:
     * key1 :  hello:123456
     * key2 :  app:webui:123456
     */
    private final Set<String> ignoreStartsWithKeys;


    public TenantStringRedisSerializer(Set<String> ignoreEqualsKeys,
                                       Set<String> ignoreStartsWithKeys,
                                       Supplier<String> getTenantId) {
        this.ignoreEqualsKeys = ignoreEqualsKeys;
        this.ignoreStartsWithKeys = ignoreStartsWithKeys;
        this.getTenantId = getTenantId;
    }


    @Override
    public byte[] serialize(String str) throws SerializationException {
        if (StrUtil.isBlank(str)) {
            return new byte[0];
        }
        var tenantId = this.getTenantId.get();
        // No tenant ID, no prefix
        if (StrUtil.isNotBlank(tenantId)) {
            var prefix = tenantId + StrPool.COLON;
            // If not included, add the tenant prefix Id
            if (!str.startsWith(prefix)) {
                // true: add the tenant prefix ID
                var flag = true;
                // Check whether ignoreEqualsKeys contains this key
                if (!ignoreEqualsKeys.isEmpty()) {
                    flag = ignoreEqualsKeys.stream().noneMatch(str::equals);
                }
                if (flag && !ignoreStartsWithKeys.isEmpty()) {
                    // Check ignoreStartsWithKeys for this key
                    flag = ignoreStartsWithKeys.stream().noneMatch(str::startsWith);
                }
                if (flag) {
                    return (prefix + str).getBytes();
                }
            }
        }
        return str.getBytes();
    }


    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        var str = new String(bytes);
        var tenantId = this.getTenantId.get();
        if (StrUtil.isNotBlank(str) && StrUtil.isNotBlank(tenantId)) {
            str = StrUtil.removePrefix(str, tenantId + StrPool.COLON);
        }
        return str;
    }


    @Override
    public Class<?> getTargetType() {
        return String.class;
    }


}
