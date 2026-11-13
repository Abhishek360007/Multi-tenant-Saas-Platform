package com.abhishek.tenanthub.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @version 1.0.0
 * @date 2020/7/2515:30
 */

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtTokenProperties {

    /**
     * 请求头的KEY。默认 "Authorization"
     */
    private String tokenHeader = "Authorization";

    /**
     * token 类型
     */
    private String tokenType = "Bearer ";

    /**
     * token 缓存 过期时间  单位(秒)
     */
    private Integer tokenExpired = 7200;

    /**
     * 短信验证码过期时间 单位(秒)
     */
    private Integer smsCodeExpired = 600;

    /**
     * 用户被挤下线，提示的过期时间 单位(秒)
     */
    private Integer promptExpired = 600;

    /**
     * 验证码 过期时间 单位(秒)
     */
    private Integer captchaExpired = 180;

    /**
     * HS256 signing secret. Leave unset to use (or generate) {@code <keys-dir>/jwt_secret}.
     * Can be supplied through the JWT_TOKEN_SECRET environment variable.
     */
    @ToString.Exclude
    private String secret;

    /**
     * Directory holding the generated JWT secret and RSA key pair. Created on first start.
     */
    private String keysDir = "./keys";

    /**
     * Optional Spring resource location of an existing RSA private key (PKCS#8 PEM),
     * e.g. file:/etc/tenanthub/rsa_private_key.pem. Leave unset to use {@code keys-dir}.
     */
    private String rsaPrivateKey;

    /**
     * Optional Spring resource location of the matching RSA public key (X.509 PEM).
     */
    private String rsaPublicKey;

    /**
     * 单点登录，是否启用
     */
    private Boolean sso = Boolean.TRUE;

    /**
     * 需要排除的URL
     */
    private String[] excludePath = {
            "/open/**"
    };

}
