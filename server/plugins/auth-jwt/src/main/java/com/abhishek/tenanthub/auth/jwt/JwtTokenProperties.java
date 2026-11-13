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
     * Request header key. Default "Authorization"
     */
    private String tokenHeader = "Authorization";

    /**
     * Token type
     */
    private String tokenType = "Bearer ";

    /**
     * Token cache expiry in seconds
     */
    private Integer tokenExpired = 7200;

    /**
     * SMS code expiry in seconds
     */
    private Integer smsCodeExpired = 600;

    /**
     * How long a forced sign-out notice is kept, in seconds
     */
    private Integer promptExpired = 600;

    /**
     * Captcha expiry in seconds
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
     * Enable single sign-on
     */
    private Boolean sso = Boolean.TRUE;

    /**
     * Excluded URL
     */
    private String[] excludePath = {
            "/open/**"
    };

}
