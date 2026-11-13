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
 * Dictionary data table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/15 10:42
 */

@Schema(description = "Dictionary data")
@TableName("sys_dict_data")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysDictData extends BaseEntity {

    /**
     * Dictionary data ID
     */
    @Schema(description = "Data ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Dictionary data ID is required", groups = {ValidUpdate.class})
    private Long dictCode;

    /**
     * Dictionary sort order
     */
    @Schema(description = "Sort order")
    @NotNull(message = "Sort order is required", groups = {ValidCreate.class})
    private Long dictSort;

    /**
     * Dictionary label
     */
    @Schema(description = "Label")
    @NotEmpty(message = "Label is required", groups = {ValidCreate.class})
    private String dictLabel;

    /**
     * Dictionary value
     */
    @Schema(description = "Key")
    @NotEmpty(message = "Value is required", groups = {ValidCreate.class})
    private String dictValue;

    /**
     * Dictionary type key
     */
    @Schema(description = "Type key")
    @NotEmpty(message = "Type key is required", groups = {ValidCreate.class})
    private String typeKey;

    /**
     * Is default (1 = yes, 0 = no)
     */
    @Schema(description = "Is default (1 = yes, 0 = no)")
    @NotNull(message = "Type key is required", groups = {ValidCreate.class})
    private Byte isDefault;

}
