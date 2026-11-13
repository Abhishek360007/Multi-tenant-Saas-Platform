package com.abhishek.tenanthub.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * <p>
 * Login log
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/12 12:08
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginLogDTO implements java.io.Serializable {

    /**
     * Message ID
     */
    private String id;

    /**
     * Authentication method
     */
    private String grantType;

    /**
     * Tenant ID
     */
    private String tenantId;

    /**
     * Operator
     */
    private Long userId;

    /**
     * User nickname
     */
    private String nickname;

    /**
     * Username
     */
    private String username;

    /**
     * Avatar
     */
    private String avatar;

    /**
     * Request url
     */
    private String requestUrl;

    /**
     * Client IP address
     */
    private String requestIp;

    /**
     * Location
     */
    private String address;


    /**
     * Operating system type
     */
    private String osName;


    /**
     * Browser
     */
    private String browser;


    /**
     * Operated at
     */
    private LocalDateTime createTime;

}
