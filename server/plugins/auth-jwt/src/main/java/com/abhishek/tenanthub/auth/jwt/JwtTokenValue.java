package com.abhishek.tenanthub.auth.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * token Return value
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @version 2.0
 * @date 2019/12/12 23:29
 */


@Schema(description = "jwt token")
@Getter
@Setter
@Builder
public class JwtTokenValue implements java.io.Serializable {

    /**
     * Request header key. Default "Authorization"
     */
    @Schema(description = "Request header KEY")
    private String header;

    /**
     * The access token. Required.
     */
    @Schema(description = "Resource token")
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * Token type, case-insensitive. Required; bearer or mac.
     */
    @Schema(description = "Token type")
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * Expiry in seconds. If omitted, expiry must be set another way.
     */
    @Schema(description = "Expiry in seconds")
    @JsonProperty("expires_in")
    private Integer expiresIn;

}
