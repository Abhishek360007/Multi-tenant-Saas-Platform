package com.abhishek.tenanthub.perms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.abhishek.tenanthub.common.consts.RegexConst;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.ParamBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * Department table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */

@Schema(description = "Department")
@TableName("pms_dept")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PmsDept extends ParamBaseEntity {

    /**
     * Department ID
     */
    @Schema(description = "Department ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Department ID is required", groups = {ValidUpdate.class})
    private Long deptId;

    /**
     * Parent department
     */
    @Schema(description = "Parent department ID")
    @NotNull(message = "Parent department ID is required", groups = {ValidCreate.class})
    private Long parentId;

    /**
     * Department name
     */
    @Schema(description = "Department name")
    @NotBlank(message = "Department name is required", groups = {ValidCreate.class})
    private String deptName;

    /**
     * Department leader name
     */
    @Schema(description = "Leader name")
    @NotBlank(message = "Leader name is required", groups = {ValidCreate.class})
    private String leader;

    /**
     * Department leader mobile
     */
    @Schema(description = "Leader mobile number")
    @NotBlank(message = "Leader mobile number is required", groups = {ValidCreate.class})
    @Pattern(regexp = RegexConst.MOBILE, message = "Leader mobile number is invalid", groups = {ValidCreate.class, ValidUpdate.class})
    private String leaderMobile;

    /**
     * Sort order
     */
    @Schema(description = "Sort order")
    @NotNull(message = "Department sort order is required", groups = {ValidCreate.class})
    private Long sort;

    /**
     * Department description
     */
    @Schema(description = "Department description")
    @NotBlank(message = "Department description is required", groups = {ValidCreate.class})
    private String description;

    /**
     * Ancestor list
     */
    @Schema(description = "Ancestor list")
    @JsonIgnore
    private String ancestors;

}
