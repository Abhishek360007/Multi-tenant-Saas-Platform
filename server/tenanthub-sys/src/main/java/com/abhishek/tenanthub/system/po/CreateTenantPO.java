package com.abhishek.tenanthub.system.po;

import com.abhishek.tenanthub.common.consts.RegexConst;
import com.abhishek.tenanthub.system.entity.SysTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/13 16:07
 */

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Used to create tenants")
public class CreateTenantPO implements java.io.Serializable {

    @Schema(description = "Unique tenant ID. Randomly generated if empty")
    private String tenantId;

    @Schema(description = "Tenant name is required", requiredMode = REQUIRED)
    @NotBlank(message = "Tenant name is required")
    private String name;

    @Schema(description = "Tenant email is required", requiredMode = REQUIRED)
    @NotBlank(message = "Tenant email is required")
    @Email(message = "Invalid email address")
    private String email;

    @Schema(description = "Tenant contact is required", requiredMode = REQUIRED)
    @NotBlank(message = "Tenant contact is required")
    private String linkman;

    @Schema(description = "Contact number is required (mobile or landline)", requiredMode = REQUIRED)
    @Pattern(regexp = RegexConst.TEL_PHONE, message = "Invalid contact number (mobile or landline)")
    private String contactNumber;

    @Schema(description = "Tenant logo; uses the default if empty logo", requiredMode = REQUIRED)
    @URL(message = "Tenant logo is invalid")
    private String logo;

    @Schema(description = "Tenant area code", requiredMode = REQUIRED)
    @NotNull(message = "Tenant area code is required")
    private Long areaCode;

    @Schema(description = "Tenant address", requiredMode = REQUIRED)
    @NotBlank(message = "Tenant address is required")
    private String address;

    @Schema(description = "6-digit PIN code", requiredMode = REQUIRED)
    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "PIN code must be 6 digits")
    private String pinCode;

    @Schema(description = "GSTIN (optional), e.g. 29ABCPE1234F1Z7")
    private String gstin;

    @Schema(description = "PAN (optional), e.g. ABCPE1234F")
    private String pan;

    @Schema(description = "Tenant permission template ID", requiredMode = REQUIRED)
    @NotNull(message = "Tenant permission template is required")
    @Size(min = 1, message = "Select at least one tenant permission template")
    private Set<SysTemplate> templates;

    @Schema(description = "Expiry time; defaults to 10 years if empty")
    private LocalDateTime expired;

    @Schema(description = "Admin name. Defaults to the tenant name if empty.")
    private String adminNickname;

    @Schema(description = "Admin login username. Randomly generated if empty.")
    private String adminUsername;

    @Schema(description = "Admin mobile number. Defaults to the tenant contact number if empty.")
    private String adminMobile;

    @Schema(description = "Admin email. Defaults to the tenant email if empty.")
    private String adminEmail;

    @Schema(description = "Admin password. A random one-time password is generated if empty.")
    private String adminPwd;

}
