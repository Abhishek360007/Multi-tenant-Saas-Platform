package com.abhishek.tenanthub.corems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.ProjectBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * Device table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2023/10/25 9:31
 */

@Schema(description = "Device")
@TableName("cms_device")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CmsDevice extends ProjectBaseEntity {

    /**
     * Device ID
     */
    @Schema(description = "Device ID")
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message = "Device ID is required", groups = {ValidUpdate.class})
    private Long deviceId;

    /**
     * Device name
     */
    @Schema(description = "Device name")
    @NotNull(message = "Device name is required", groups = {ValidCreate.class})
    private String deviceName;

    /**
     * Status [0 = disabled, 1 = normal, 2 = locked]
     */
    @Schema(description = "Status [0 = disabled, 1 = normal, 2 = locked]")
    @NotNull(message = "Status is required", groups = {ValidCreate.class})
    private Byte status;

}
