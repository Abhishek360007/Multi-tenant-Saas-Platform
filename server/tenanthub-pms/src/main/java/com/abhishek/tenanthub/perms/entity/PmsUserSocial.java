package com.abhishek.tenanthub.perms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * User social account
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Schema(description = "User social account")
@TableName("pms_user_social")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PmsUserSocial extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "Social account ID")
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message = "Social account ID is required", groups = {ValidUpdate.class})
    private Long socialId;

    /**
     * User ID
     */
    @Schema(description = "User ID")
    @NotNull(message = "User ID is required", groups = {ValidCreate.class})
    private Long userId;

    /**
     * Social account is unique ID
     */
    @Schema(description = "Social account is unique ID")
    @NotBlank(message = "Social account unique ID is required", groups = {ValidCreate.class})
    private String openId;

    /**
     * we_chat. WeChat  tencent_qq. Tencent QQ
     */
    @Schema(description = "we_chat: WeChat  tencent_qq: Tencent QQ")
    @NotBlank(message = "Social login type is required", groups = {ValidCreate.class})
    private String socialType;

}
