package com.abhishek.tenanthub.system.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Create tenant
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/16 12:02
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Tenant creation result")
public class TenantCreatedSuccessVO implements java.io.Serializable {

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
