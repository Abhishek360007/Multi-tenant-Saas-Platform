package com.abhishek.tenanthub.auth.service;

import com.abhishek.tenanthub.auth.exception.JwtAuthException;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtTokenValue;
import com.abhishek.tenanthub.auth.po.LoginFormPO;
import com.abhishek.tenanthub.auth.po.OtpLoginPO;
import com.abhishek.tenanthub.auth.po.OtpSendPO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;


/**
 * <p>
 * User login service interface
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


public interface UserLoginService {

    /**
     * User login
     *
     * @param po
     * @param request
     * @return TokenValue Value
     * @throws AuthenticationException
     */
    JwtTokenValue login(LoginFormPO po, HttpServletRequest request) throws JwtAuthException;

    /**
     * Send a sign-in OTP by email or SMS (captcha required). Responds the same whether or not the account exists.
     */
    void sendOtp(OtpSendPO po, HttpServletRequest request) throws JwtAuthException;

    /**
     * Sign in with an OTP.
     */
    JwtTokenValue loginWithOtp(OtpLoginPO po, HttpServletRequest request) throws JwtAuthException;


    /**
     * Refresh the in-memory permissions of the user
     */
    void refreshAuthoritys();


    /**
     * Refresh the in-memory permissions of the user
     */
    void refreshAuthoritys(JwtLoginUser loginUser);


}
