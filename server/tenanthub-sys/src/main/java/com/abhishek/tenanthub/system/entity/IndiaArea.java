package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * India administrative areas
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Schema(description = "India administrative area (state, district or sub-district) from the Local Government Directory")
@TableName("india_area")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IndiaArea implements java.io.Serializable {

    /**
     * Unique code: state * 1e9 + district * 1e5 + subdistrict (LGD codes)
     */
    @TableId
    @Schema(description = "Area code (state*1e9 + district*1e5 + subdistrict)")
    private Long areaCode;

    @Schema(description = "Level: 1 = state/UT, 2 = district, 3 = sub-district")
    private Integer level;

    @Schema(description = "Area code of the parent (0 for states/UTs)")
    private Long parentCode;

    @Schema(description = "Local Government Directory (LGD) code at this level")
    private Integer lgdCode;

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Full path, e.g. Karnataka-Bengaluru Urban-Bengaluru North")
    private String mergerName;

    @Schema(description = "State or UT (level 1 only)")
    private String type;
}
