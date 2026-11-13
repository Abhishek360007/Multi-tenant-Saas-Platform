package com.abhishek.tenanthub.corems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.consts.RegexConst;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidDelete;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.internal.dto.PmsDeptDTO;
import com.abhishek.tenanthub.tenant.base.ParamBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * Project table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2023/10/10 9:31
 */

@Schema(description = "Project")
@TableName("cms_project")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CmsProject extends ParamBaseEntity {

    /**
     * Project ID
     */
    @Schema(description = "Project ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "Project ID is required", groups = {ValidUpdate.class, ValidDelete.class})
    private Long projectId;

    /**
     * Project name
     */
    @Schema(description = "Project name")
    @NotEmpty(message = "Project name is required", groups = {ValidCreate.class})
    private String projectName;

    /**
     * Contact name
     */
    @Schema(description = "Contact name")
    @NotEmpty(message = "Contact name is required", groups = {ValidCreate.class})
    private String linkman;

    /**
     * Contact number
     */
    @Schema(description = "Contact number")
    @NotEmpty(message = "Contact number is required", groups = {ValidCreate.class})
    @Pattern(regexp = RegexConst.TEL_PHONE, message = "Contact number is invalid", groups = {ValidCreate.class, ValidUpdate.class})
    private String contactNumber;

    /**
     * Address area
     */
    @Schema(description = "Address area")
    @NotNull(message = "Area is required", groups = {ValidCreate.class})
    private Long areaCode;

    /**
     * Address, e.g. 12 MG Road, Block A
     */
    @Schema(description = "Address")
    @NotEmpty(message = "Address is required", groups = {ValidCreate.class})
    private String address;

    /**
     * 6-digit PIN code
     */
    @Schema(description = "6-digit PIN code")
    @NotEmpty(message = "PIN code is required", groups = {ValidCreate.class})
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "PIN code must be 6 digits", groups = {ValidCreate.class, ValidUpdate.class})
    private String pinCode;

    /**
     * Sort order
     */
    @Schema(description = "Sort order")
    private Long sort;

    /**
     * Project password, required for dangerous operations
     */
    @Schema(description = "Project password, required for dangerous operations")
    @NotEmpty(message = "Project password is required", groups = {ValidDelete.class})
    private String password;

    /**
     * Status [0 = disabled, 1 = normal, 2 = locked]
     */
    @Schema(description = "Status [0 = disabled, 1 = normal, 2 = locked]")
    private Byte status;

    /**
     * Department
     */
    @Schema(description = "Department Id")
    @NotNull(message = "Department ID is required", groups = {ValidCreate.class})
    private Long deptId;

    /**
     * Department
     */
    @Schema(description = "Department")
    @TableField(exist = false)
    private PmsDeptDTO dept;

}
