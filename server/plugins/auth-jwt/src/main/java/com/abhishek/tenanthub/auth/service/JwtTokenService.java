package com.abhishek.tenanthub.auth.service;

import com.abhishek.tenanthub.auth.exception.JwtAuthException;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtTokenProperties;
import com.abhishek.tenanthub.auth.jwt.JwtTokenValue;

import java.util.Map;
import java.util.Set;


/**
 * <p>
 * Server-side token authentication
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/12 16:28
 */


public interface JwtTokenService {

    /**
     * Store the signed-in user in Redis
     *
     * @param loginUser
     */
    void setLoginUser(JwtLoginUser loginUser);


    /**
     * Update the signed-in user in Redis
     *
     * @param loginUser
     */
    void updateLoginUser(JwtLoginUser loginUser);


    /**
     * Get the user by loginId
     * <p>
     * e.g.: login_tokens:c6ea9a4cbb0e4fd093764c87fcfb372f
     *
     * @param loginId
     */
    JwtLoginUser getLoginUser(String loginId);


    /**
     * Get the user by username
     *
     * @return String User name
     * @author Wang Chen Chen
     * @date 2021/7/12 16:29
     */
    JwtLoginUser getLoginUserByUsername(String username);


    /**
     * Validate the token
     * <p>
     * e.g.: Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdG6IjE2NDPuFA
     *
     * @param bearerToken
     * @author Wang Chen Chen
     * @date 2021/7/12 16:29
     */
    JwtLoginUser validate(String bearerToken) throws JwtAuthException;


    /**
     * Refresh token
     */
    JwtTokenValue refresh();


    /**
     * Sign out
     */
    void logout();


    /**
     * Create a JWT string
     */
    String createJwtStr(String id);


    /**
     * Get the online users of the current tenant
     *
     * @return String User name
     * @author Wang Chen Chen
     * @date 2021/7/12 16:29
     */
    Set<String> listLoginUsername();


    /**
     * Get all online usernames
     *
     * @return String User name
     * @author Wang Chen Chen
     * @date 2021/7/12 16:29
     */
    Set<String> listLoginIds();


    /**
     * Get the online users of the current tenant
     * key: User ID
     * value: User details
     *
     * @return String User name
     * @author Wang Chen Chen
     * @date 2021/7/12 16:29
     */
    Map<Long, JwtLoginUser> mapLoginUser();


    /**
     * Get the online users of all tenants
     * key: User ID
     * value: User details
     *
     * @return String User name
     * @author Wang Chen Chen
     * @date 2021/7/12 16:29
     */
    Map<Long, JwtLoginUser> mapAllLoginUser();


    /**
     * Get the JWT settings
     *
     * @author Wang Chen Chen
     * @date 2021/7/12 16:29
     */
    JwtTokenProperties getProps();


}
