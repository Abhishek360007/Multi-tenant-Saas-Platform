package com.abhishek.tenanthub.perms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.consts.RegexConst;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.ParamBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * <p>
 * Platform user
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Schema(description = "User")
@TableName("pms_user")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PmsUser extends ParamBaseEntity {

    /**
     * User ID
     */
    @Schema(description = "User ID")
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message = "User ID is required", groups = {ValidUpdate.class})
    private Long userId;

    /**
     * Avatar
     */
    @Schema(description = "Avatar")
    @NotBlank(message = "Avatar is required", groups = {ValidCreate.class})
    @URL(message = "Avatar is invalid", groups = {ValidCreate.class, ValidUpdate.class})
    private String avatar;

    /**
     * Username
     */
    @Schema(description = "Username")
    @NotBlank(message = "Username is required", groups = {ValidCreate.class})
    @Length(min = 5, max = 32, message = "Username must be 5 to 32 characters", groups = {ValidCreate.class, ValidUpdate.class})
    private String username;

    /**
     * Mobile number
     */
    @Schema(description = "Mobile number")
    @NotBlank(message = "Mobile number is required", groups = {ValidCreate.class})
    @Pattern(regexp = RegexConst.MOBILE, message = "Mobile number is invalid", groups = {ValidCreate.class, ValidUpdate.class})
    private String mobile;

    /**
     * Email
     */
    @Schema(description = "Email")
    @NotBlank(message = "Email is required", groups = {ValidCreate.class})
    @Email(message = "Email is invalid", groups = {ValidCreate.class, ValidUpdate.class})
    private String email;

    /**
     * Nickname
     */
    @Schema(description = "Nickname")
    @NotBlank(message = "Nickname is required", groups = {ValidCreate.class})
    private String nickname;

    /**
     * Password
     */
    @Schema(description = "Password")
    @Length(min = 5, max = 32, message = "Password must be 5 to 32 characters", groups = {ValidCreate.class, ValidUpdate.class})
    private String password;

    /**
     * Gender [0 = female, 1 = male, 2 = unknown]
     */
    @Schema(description = "Gender [0 = female, 1 = male, 2 = unknown]")
    @NotNull(message = "Gender is required", groups = {ValidCreate.class})
    private Byte gender;

    /**
     * Is admin: 0 = regular user, 1 = admin
     */
    @Schema(description = "Is admin: 0 = regular user, 1 = admin")
    private Byte adminFlag;

    /**
     * Status [0 = disabled, 1 = normal]
     */
    @Schema(description = "Status [0 = disabled, 1 = normal]")
    private Byte status;

    /**
     * Department
     */
    @Schema(description = "Department Id")
    @NotNull(message = "Department ID is required", groups = {ValidCreate.class})
    private Long deptId;

    /**
     * 1 = the user must change their password at next login. Server-managed: ignored in request bodies.
     */
    @Schema(description = "Must change password at next login (1 = yes)", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Byte passwordChangeRequired;

    /**
     * Expiry time (empty = never expires)
     */
    @Schema(description = "Expiry time; empty means never expires")
    private LocalDateTime expired;

    /**
     * Department details
     */
    @Schema(description = "Department details")
    @TableField(exist = false)
    private PmsDept dept;

    /**
     * Role names
     */
    @Schema(description = "Roles")
    @TableField(exist = false)
    @NotNull(message = "Roles are required", groups = {ValidCreate.class})
    @Size(min = 1, message = "Roles: select at least 1", groups = {ValidCreate.class, ValidUpdate.class})
    private Set<PmsRole> roles;

    /**
     * Empty means not signed in.
     */
    @Schema(description = "Empty means not signed in")
    @TableField(exist = false)
    private String loginId;

}
