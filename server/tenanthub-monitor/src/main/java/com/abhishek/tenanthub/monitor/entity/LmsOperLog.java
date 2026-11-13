package com.abhishek.tenanthub.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * Operation log
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/6 15:27
 */

@TableName("lms_oper_log")
@Schema(description = "User operation log")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LmsOperLog implements java.io.Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;

    /**
     * Title
     */
    @Schema(description = "Title")
    private String title;

    /**
     * Description
     */
    @Schema(description = "Description")
    private String description;

    /**
     * Service name
     */
    @Schema(description = "Service name")
    private String serviceName;

    /**
     * User ID
     */
    @Schema(description = "User ID")
    private Long userId;

    /**
     * User nickname
     */
    @Schema(description = "User nickname")
    @TableField(exist = false)
    private String nickname;

    /**
     * Username
     */
    @Schema(description = "User nickname")
    @TableField(exist = false)
    private String username;

    /**
     * Avatar
     */
    @Schema(description = "User nickname")
    @TableField(exist = false)
    private String avatar;

    /**
     * Method
     */
    @Schema(description = "Method name")
    private String method;

    /**
     * Method arguments
     */
    @Schema(description = "Method arguments")
    private String methodArgs;

    /**
     * Request method
     */
    @Schema(description = "http Request method")
    private String requestMethod;

    /**
     * Request url
     */
    @Schema(description = "Request url")
    private String requestUrl;

    /**
     * Request IP
     */
    @Schema(description = "Request IP")
    private String requestIp;

    /**
     * Request URL
     */
    @Schema(description = "Request URL")
    private String address;

    /**
     * Response
     */
    @Schema(description = "User nickname")
    private String responseResult;

    /**
     * Status
     */
    @Schema(description = "Result status")
    private Integer status;

    /**
     * Error log
     */
    @Schema(description = "Error log")
    private String errorLog;

    /**
     * Duration (ms)
     */
    @Schema(description = "Duration (ms)")
    private Long timeCost;

    /**
     * Created at
     */
    @Schema(description = "Created at")
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

}
