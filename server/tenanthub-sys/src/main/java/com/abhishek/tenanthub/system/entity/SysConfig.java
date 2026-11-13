package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * Common parent entity
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Schema(description = "Global settings")
@TableName("sys_config")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysConfig extends BaseEntity {

    /**
     * Parameter key
     */
    @Schema(description = "Settings ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Config ID is required", groups = {ValidUpdate.class})
    private Long configId;

    /**
     * Parameter name
     */
    @Schema(description = "Parameter name")
    @NotEmpty(message = "Parameter name is required", groups = {ValidCreate.class})
    private String configName;

    /**
     * Parameter key
     */
    @Schema(description = "Parameter key")
    @NotEmpty(message = "Parameter key is required", groups = {ValidCreate.class})
    private String configKey;

    /**
     * Parameter value
     */
    @Schema(description = "Parameter value")
    @NotEmpty(message = "Parameter value is required", groups = {ValidCreate.class})
    private String configValue;

    /**
     * Built in (1 = yes, 0 = no)
     */
    @Schema(description = "Built in (1 = yes, 0 = no)")
    @NotNull(message = "Built-in flag is required", groups = {ValidCreate.class})
    private Byte configType;

}
