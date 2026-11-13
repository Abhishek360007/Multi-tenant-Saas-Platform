package com.abhishek.tenanthub.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * Login log
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/6 15:27
 */

@Schema(description = "User login log")
@TableName("lms_login_log")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LmsLoginLog implements java.io.Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;

    /**
     * Grant type
     */
    @Schema(description = "Grant type")
    private String grantType;

    /**
     * User ID
     */
    @Schema(description = "User ID")
    private Long userId;

    /**
     * User nickname
     */
    @Schema(description = "User nickname")
    private String nickname;

    /**
     * Username
     */
    @Schema(description = "Username")
    private String username;

    /**
     * Avatar
     */
    @Schema(description = "User avatar")
    private String avatar;

    /**
     * Request URL
     */
    @Schema(description = "Request URL")
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
     * Operating system
     */
    @Schema(description = "Operating system")
    private String osName;

    /**
     * Browser
     */
    @Schema(description = "Browser")
    private String browser;

    /**
     * Created at
     */
    @Schema(description = "Created at")
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * Tenant ID
     */
    @TableField(exist = false)
    @JsonIgnore
    private String tenantId;

}
