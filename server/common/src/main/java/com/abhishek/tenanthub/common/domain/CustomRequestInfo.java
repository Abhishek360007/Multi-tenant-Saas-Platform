package com.abhishek.tenanthub.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * <p>
 * Request log
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/12 12:08
 */

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CustomRequestInfo implements java.io.Serializable {

    /**
     * Request method  GET POST PUT DELETE
     */
    @Schema(description = "Request method")
    private String method;

    /**
     * Request url
     */
    @Schema(description = "Request url")
    private String requestUrl;

    /**
     * Client IP address
     */
    @Schema(description = "ip Address")
    private String ip;

    /**
     * Operating system type
     */
    @Schema(description = "Operating system")
    private String osName;

    /**
     * Browser
     */
    @Schema(description = "Browser")
    private String browser;

    /**
     * Address
     */
    @Schema(description = "Real address")
    private String address;

}
