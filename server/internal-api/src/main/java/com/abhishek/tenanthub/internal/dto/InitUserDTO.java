package com.abhishek.tenanthub.internal.dto;

import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * Tenant
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class InitUserDTO implements java.io.Serializable {

    // Tenant ID
    private String tenantId;

    //  Tenant name is required
    private String name;

    //  Tenant name is required
    private String logo;

    // Admin name. Defaults to the tenant name if empty.
    private String adminNickname;

    // Admin login username. Randomly generated if empty.
    private String adminUsername;

    // Admin mobile number. Defaults to the tenant contact number if empty.
    private String adminMobile;

    // Admin email. Defaults to the tenant email if empty.
    private String adminEmail;

    // Admin password. A random one-time password is generated if empty.
    private String adminPwd;

}
