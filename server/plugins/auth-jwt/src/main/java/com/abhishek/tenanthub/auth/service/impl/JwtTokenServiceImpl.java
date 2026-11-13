package com.abhishek.tenanthub.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.abhishek.tenanthub.auth.exception.JwtAuthException;
import com.abhishek.tenanthub.auth.jwt.AuthKeyMaterial;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.auth.jwt.JwtTokenProperties;
import com.abhishek.tenanthub.auth.jwt.JwtTokenValue;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.auth.enums.OAuth2Error.TOKEN_FORMAT_ERROR;
import static com.abhishek.tenanthub.common.consts.LoginConst.*;


/**
 * <p>
 * Server-side JWT authentication
 * </p>
 *
 * @author WangChenChen
 * @version 1.0
 * @date 2022/3/22 15:24
 */

@Slf4j
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtTokenProperties props;

    private final StringRedisTemplate strRedisTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JWTSigner signer;


    public JwtTokenServiceImpl(JwtTokenProperties props,
                               RedisTemplate<String, Object> redisTemplate,
                               StringRedisTemplate strRedisTemplate) {
        this.props = props;
        this.strRedisTemplate = strRedisTemplate;
        this.redisTemplate = redisTemplate;
        this.signer = JWTSignerUtil.hs256(AuthKeyMaterial.jwtSecret(props));
    }


    private void removeLoginUser(String loginId) {
        strRedisTemplate.delete(loginId);
    }


    @Override
    public JwtLoginUser getLoginUser(String loginId) {
        var obj = redisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + loginId);
        if (obj == null) {
            return null;
        }
        return BeanUtil.copyProperties(obj, JwtLoginUser.class);
    }


    @Override
    public JwtLoginUser getLoginUserByUsername(String username) {
        var onlineUserKey = ONLINE_USER_KEY + username;
        var loginId = strRedisTemplate.opsForValue().get(onlineUserKey);
        if (StringUtils.isEmpty(loginId)) {
            return null;
        }
        return getLoginUser(loginId);
    }


    @Override
    public void setLoginUser(JwtLoginUser loginUser) {
        var loginKey = LOGIN_TOKEN_KEY + loginUser.getLoginId();

        // Link the random ID to the signed-in user
        redisTemplate.opsForValue().set(loginKey, loginUser, Duration.ofSeconds(props.getTokenExpired()));

        // Check whether single sign-on is enabled
        if (props.getSso()) {
            // Build the current online user online_user:admin
            var onlineUserKey = ONLINE_USER_KEY + loginUser.getUsername();
            var oldLoginKey = strRedisTemplate.opsForValue().get(onlineUserKey);
            // Check whether this username is already signed in
            if (StringUtils.isNotBlank(oldLoginKey)) {
                // Remove the previous sign-in
                removeLoginUser(LOGIN_TOKEN_KEY + oldLoginKey);

                // Remove the online user
                removeLoginUser(onlineUserKey);

                // Get the current time
                var milli = LocalDateTimeUtil.formatNormal(LocalDateTime.now());

                // Store the forced-out user and time in Redis so the front end can notify them
                strRedisTemplate.opsForValue().set(
                        FORCED_OFFLINE_KEY + oldLoginKey,
                        milli,
                        Duration.ofSeconds(props.getPromptExpired())
                );
            }
            // Save the online user
            strRedisTemplate.opsForValue().set(onlineUserKey, loginUser.getLoginId(), Duration.ofSeconds(props.getTokenExpired()));
        }
    }


    @Override
    public void updateLoginUser(JwtLoginUser loginUser) {
        var loginKey = LOGIN_TOKEN_KEY + loginUser.getLoginId();
        var expire = redisTemplate.getExpire(loginKey);
        if (expire != null && expire > 0) {
            redisTemplate.opsForValue().set(loginKey, loginUser, Duration.ofSeconds(expire));
        }
    }


    @Override
    public JwtLoginUser validate(String bearerToken) throws JwtAuthException {
        var tokenStr = bearerToken.substring(props.getTokenType().length());
        JWT jwt = null;
        try {
            jwt = JWTUtil.parseToken(tokenStr);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new JwtAuthException(TOKEN_FORMAT_ERROR);
        }
        // Get the unique login of the user ID
        var loginId = jwt.getPayloads().getStr(JWT.SUBJECT);

        // Get the signed-in user from Redis by unique login ID
        var jwtUser = getLoginUser(loginId);

        // If the user is empty, check whether single sign-on is enabled
        if (jwtUser == null || StringUtils.isEmpty(jwtUser.getUsername())) {
            // Check whether single sign-on is enabled
            if (props.getSso()) {
                var forcedOfflineKey = FORCED_OFFLINE_KEY + loginId;
                // Check whether this user was forced out
                var offlineTime = strRedisTemplate.opsForValue().get(forcedOfflineKey);
                if (StringUtils.isNotBlank(offlineTime)) {
                    // Delete the forced sign-out notice
                    removeLoginUser(forcedOfflineKey);
                    var errMsg = String.format("Your account was signed out at [ %s ] because it signed in elsewhere", offlineTime);
                    log.warn("errMsg {}", errMsg);
                    throw new JwtAuthException(errMsg);
                }
            }
            throw new JwtAuthException("The signed-in user does not exist");
        }
        var loginKey = LOGIN_TOKEN_KEY + loginId;
        // Token expiry
        var expire = strRedisTemplate.getExpire(loginKey);
        // If less than 10 minutes remain, extend the token to 60 minutes
        if (expire != null && expire < props.getPromptExpired()) {
            strRedisTemplate.expire(loginKey, Duration.ofHours(1));
        }
        return jwtUser;
    }


    @Override
    public JwtTokenValue refresh() {
        var oldLoginUser = JwtSecurityUtils.getLoginUser();

        var loginKey = LOGIN_TOKEN_KEY + oldLoginUser.getLoginId();

        // Remove the signed-in user by tokenId
        removeLoginUser(loginKey);

        // Generate a random ID linked to the current user
        var loginId = IdUtil.simpleUUID();
        var token = createJwtStr(loginId);
        oldLoginUser.setLoginId(loginId);

        setLoginUser(oldLoginUser);

        return JwtTokenValue.builder()
                .header(props.getTokenHeader())
                .accessToken(token)
                .tokenType(props.getTokenType())
                .expiresIn(props.getTokenExpired())
                .build();
    }


    @Override
    public void logout() {
        var loginUser = JwtSecurityUtils.getLoginUser();
        var loginKey = LOGIN_TOKEN_KEY + loginUser.getLoginId();
        // Remove the signed-in user by tokenId
        removeLoginUser(loginKey);
        // Build the current online user online_user:admin
        var onlineUserKey = ONLINE_USER_KEY + loginUser.getUsername();
        removeLoginUser(onlineUserKey);
        SecurityContextHolder.clearContext();
    }


    @Override
    public String createJwtStr(String id) {
        return JWT.create().setSigner(signer).setSubject(id).sign();
    }


    @Override
    public Set<String> listLoginUsername() {
        var onlineUserKey = ONLINE_USER_KEY + "*";
        return Objects.requireNonNull(strRedisTemplate.keys(onlineUserKey))
                .stream()
                .map(r -> r.replaceAll(ONLINE_USER_KEY, ""))
                .collect(Collectors.toSet());
    }


    @Override
    public Set<String> listLoginIds() {
        var onlineLoginKey = LOGIN_TOKEN_KEY + "*";
        return Objects.requireNonNull(strRedisTemplate.keys(onlineLoginKey))
                .stream()
                .map(r -> r.replaceAll(LOGIN_TOKEN_KEY, ""))
                .collect(Collectors.toSet());
    }


    @Override
    public Map<Long, JwtLoginUser> mapLoginUser() {
        var onlineUserKey = ONLINE_USER_KEY + "*";
        var keys = strRedisTemplate.keys(onlineUserKey);
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>();
        }
        var loginIds = strRedisTemplate.opsForValue().multiGet(keys);
        if (loginIds == null || loginIds.isEmpty()) {
            return new HashMap<>();
        }
        var loginUserKeys = loginIds
                .stream()
                .map(loginId -> LOGIN_TOKEN_KEY + loginId)
                .collect(Collectors.toSet());
        return mapLoginUser(loginUserKeys);
    }


    @Override
    public Map<Long, JwtLoginUser> mapAllLoginUser() {
        var keys = strRedisTemplate.keys(LOGIN_TOKEN_KEY + "*");
        return mapLoginUser(keys);
    }


    private Map<Long, JwtLoginUser> mapLoginUser(Set<String> keys) {
        var result = new HashMap<Long, JwtLoginUser>();
        if (keys == null || keys.isEmpty()) {
            return result;
        }
        var objects = redisTemplate.opsForValue().multiGet(keys);
        if (objects == null || objects.isEmpty()) {
            return result;
        }
        return objects.stream()
                .filter(Objects::nonNull)
                .map(a -> BeanUtil.copyProperties(a, JwtLoginUser.class))
                .collect(Collectors.toMap(JwtLoginUser::getUserId, a -> a));
    }


    @Override
    public JwtTokenProperties getProps() {
        return props;
    }


}
