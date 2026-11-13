package com.abhishek.tenanthub.tenant.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.abhishek.tenanthub.internal.dto.OperateUserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 13:39
 */


@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntity implements java.io.Serializable {

    /**
     * Created at
     */
    @Schema(description = "Created at")
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * Created by id
     */
    @Schema(description = "Created by id")
    @TableField(fill = FieldFill.INSERT)
    protected Long createUser;

    /**
     * Creator details
     */
    @Schema(description = "Creator details")
    @TableField(exist = false)
    protected OperateUserDTO createUserEntity;

    /**
     * Last updated at
     */
    @Schema(description = "Last updated at")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime lastUpdateTime;

    /**
     * Last updated by id
     */
    @Schema(description = "Last updated by id")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Long lastUpdateUser;

    /**
     * Last updater details
     */
    @Schema(description = "Last updater details")
    @TableField(exist = false)
    protected OperateUserDTO lastUpdateUserEntity;

}
