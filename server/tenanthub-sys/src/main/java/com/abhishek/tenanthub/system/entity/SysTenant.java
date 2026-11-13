package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abhishek.tenanthub.common.consts.RegexConst;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.tenant.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.Set;


/**
 * <p>
 * Tenant
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Schema(description = "Tenant")
@TableName("sys_tenant")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysTenant extends BaseEntity {

    /**
     * Tenant ID
     */
    @Schema(description = "Tenant ID")
    @TableId(type = IdType.INPUT)
    @NotEmpty(message = "Tenant ID is required", groups = {ValidUpdate.class})
    private String tenantId;

    /**
     * Tenant logo
     */
    @Schema(description = "Tenant logo")
    @NotEmpty(message = "Tenant logo is required", groups = {ValidCreate.class})
    @URL(message = "Tenant logo is invalid", groups = {ValidCreate.class, ValidUpdate.class})
    private String logo;

    /**
     * Tenant name
     */
    @Schema(description = "Tenant name")
    @NotEmpty(message = "Tenant name is required", groups = {ValidCreate.class})
    private String name;

    /**
     * Tenant email
     */
    @Schema(description = "Tenant email")
    @NotEmpty(message = "Tenant email is required", groups = {ValidCreate.class})
    private String email;

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
     * Address
     */
    @Schema(description = "Address")
    @NotEmpty(message = "Address is required", groups = {ValidCreate.class})
    private String address;

    @Schema(description = "6-digit PIN code")
    @NotEmpty(message = "PIN code is required", groups = {ValidCreate.class})
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "PIN code must be 6 digits", groups = {ValidCreate.class, ValidUpdate.class})
    private String pinCode;

    @Schema(description = "GSTIN (optional), e.g. 29ABCPE1234F1Z7")
    private String gstin;

    @Schema(description = "PAN (optional), e.g. ABCPE1234F")
    private String pan;

    /**
     * Status [0 = disabled, 1 = normal, 9 = initializing]
     */
    @Schema(description = "Status [0 = disabled, 1 = normal, 9 = initializing]")
    private Byte status;

    /**
     * Expiry time (default 100 years)
     */
    @Schema(description = "Expiry time (default 10 years)")
    private LocalDateTime expired;

    /**
     * Tenant template
     */
    @Schema(description = "Tenant template")
    @TableField(exist = false)
    @NotNull(message = "Tenant template is required", groups = {ValidCreate.class})
    @Size(min = 1, message = "Select at least 1 tenant template", groups = {ValidCreate.class, ValidUpdate.class})
    private Set<SysTemplate> templates;

}
