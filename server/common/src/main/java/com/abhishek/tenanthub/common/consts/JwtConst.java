package com.abhishek.tenanthub.common.consts;


import static com.abhishek.tenanthub.common.consts.WebSocketConst.STOMP_ENDPOINT;

public class JwtConst {

    // Sign-in URL
    public static final String LOGIN_URL = "/auth/login";

    // OTP sign-in endpoints (send + verify); like LOGIN_URL they need no token but do need the tenant
    public static final String OTP_URLS = "/auth/otp/**";

    // Sign-out URL
    public static final String LOGOUT_URL = "/auth/logout";

    public static final String LOGIN_USER_URL = "/auth/login/user";

    // Refresh token
    public static final String REFRESH_URL = "/auth/refresh";

    // Captcha
    public static final String CAPTCHA_CODES_URL = "/auth/captcha/codes";

    // Get public key
    public static final String PUBLIC_KEY_URL = "/auth/public/key";

    // Paths that skip authentication
    public static final String[] WHITE_LIST = {
            LOGIN_URL,
            // OTP sign-in (send + verify)
            OTP_URLS,
            PUBLIC_KEY_URL,
            String.format("%s/**", CAPTCHA_CODES_URL),
            String.format("%s/**", STOMP_ENDPOINT),
            "/actuator/**",
            // Check whether a tenant exists
            "/sys/tenant/simple",
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/doc.html",
            "/configuration/ui",
            "/swagger-resources",
            "/configuration/security",
            "/webjars/**",
            "/swagger-resources/configuration/ui",
            "/swagger-ui.html",
            "/error",
            "/error/**",
            "/files/**",
    };


    // Paths that do not need a tenant
    public static final String[] TENANT_WHITE_LIST = {
            "/upload/**",
            "/files/**",
    };


}
