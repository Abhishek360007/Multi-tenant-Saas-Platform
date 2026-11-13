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
 * Dictionary type table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/15 10:42
 */

@Schema(description = "Dictionary type")
@TableName("sys_dict_type")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysDictType extends BaseEntity {

    /**
     * Dictionary type ID
     */
    @Schema(description = "Dictionary type ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Dictionary type ID is required", groups = {ValidUpdate.class})
    private Long typeId;

    /**
     * Dictionary type name
     */
    @Schema(description = "Type name")
    @NotEmpty(message = "Type name is required", groups = {ValidCreate.class})
    private String typeName;

    /**
     * Dictionary type key
     */
    @Schema(description = "Keyword")
    @NotEmpty(message = "Key is required", groups = {ValidCreate.class})
    private String typeKey;

    /**
     * Dictionary description
     */
    @Schema(description = "Description")
    @NotEmpty(message = "Description is required", groups = {ValidCreate.class})
    private String description;

}
