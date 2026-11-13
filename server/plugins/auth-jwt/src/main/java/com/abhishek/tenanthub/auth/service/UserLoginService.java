package com.abhishek.tenanthub.auth.service;

import com.abhishek.tenanthub.auth.exception.JwtAuthException;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtTokenValue;
import com.abhishek.tenanthub.auth.po.LoginFormPO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;


/**
 * <p>
 * 用户 登录 Service 接口
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


public interface UserLoginService {

    /**
     * 用户登录
     *
     * @param po
     * @param request
     * @return TokenValue 值
     * @throws AuthenticationException
     */
    JwtTokenValue login(LoginFormPO po, HttpServletRequest request) throws JwtAuthException;


    /**
     * 刷新内存中用户的权限
     */
    void refreshAuthoritys();


    /**
     * 刷新内存中用户的权限
     */
    void refreshAuthoritys(JwtLoginUser loginUser);


}
