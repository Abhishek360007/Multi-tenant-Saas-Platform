package com.abhishek.tenanthub.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Operation log table oper_log
 *
 * @author wangwei
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperLogDTO implements java.io.Serializable {

    /**
     * Message ID
     */
    private String id;

    /**
     * Title
     */
    private String title;

    /**
     * Description
     */
    private String description;

    /**
     * Service name
     */
    private String serviceName;

    /**
     * Tenant ID
     */
    private String tenantId;

    /**
     * Operator
     */
    private Long userId;

    /**
     * Operator
     */
    private String nickname;

    /**
     * Method name
     */
    private String method;

    /**
     * Method arguments
     */
    private List<Map<String, Object>> methodArgs;

    /**
     * Request method
     */
    private String requestMethod;

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
     * Response
     */
    private Map<String, Object> responseResult;

    /**
     * Operation status (200 = normal, otherwise error)
     */
    private Integer status;

    /**
     * Error log
     */
    private String errorLog;

    /**
     * Duration
     */
    private Long timeCost;

    /**
     * Operated at
     */
    private LocalDateTime createTime;

}
