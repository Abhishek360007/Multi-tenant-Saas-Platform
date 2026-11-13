package com.abhishek.tenanthub.auth.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.abhishek.tenanthub.auth.enums.GrantType;
import com.abhishek.tenanthub.common.enums.AdminFlag;
import com.abhishek.tenanthub.common.enums.StatusEnum;
import com.abhishek.tenanthub.common.enums.UserType;
import com.abhishek.tenanthub.internal.dto.PmsRoleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/**
 * <p>
 * spring security UserDetails implementation
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */

@Schema(description = "Signed-in user")
@ToString
@Getter
@Setter
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtLoginUser implements UserDetails, Principal {

    /**
     * Tenant ID
     */
    @Schema(description = "Tenant ID")
    private String tenantId;

    /**
     * Sign in ID
     */
    @Schema(description = "Unique login ID")
    private String loginId;

    /**
     * Authentication method
     */
    @Schema(description = "Grant type")
    private GrantType grantType;

    /**
     * User ID
     */
    @Schema(description = "User ID")
    private Long userId;

    /**
     * Avatar
     */
    @Schema(description = "User avatar")
    private String avatar;

    /**
     * Username
     */
    @Schema(description = "Username")
    private String username;

    /**
     * Mobile number
     */
    @Schema(description = "Mobile number")
    private String mobile;

    /**
     * Email
     */
    @Schema(description = "User email")
    private String email;

    /**
     * Nickname
     */
    @Schema(description = "User nickname")
    private String nickname;

    /**
     * Password
     */
    @Schema(description = "Password")
    @JsonIgnore
    private String password;

    /**
     * User status [0 = disabled, 1 = normal, 2 = deleted ]
     */
    @Schema(description = "User status [0 = disabled, 1 = normal, 2 = deleted ]")
    private StatusEnum status;

    /**
     * Is admin: 0 = regular user, 1 = admin
     */
    @Schema(description = "Is admin: 0 = regular user, 1 = admin")
    private AdminFlag adminFlag;

    /**
     * User type: 0 = tenant user, 1 = platform user
     */
    @Schema(description = "User type: 0 = tenant user, 1 = platform user")
    private UserType userType;

    /**
     * Gender [0 = female, 1 = male, 2 = unknown]
     */
    @Schema(description = "Gender [0 = female, 1 = male, 2 = unknown]")
    private Byte gender;

    /**
     * Department
     */
    @Schema(description = "Department")
    private Long deptId;

    /**
     * 1 = the user must change their password before using anything else
     */
    @Schema(description = "Must change password before continuing (1 = yes)")
    private Byte passwordChangeRequired;

    /**
     * Expiry time (empty = never expires)
     */
    @Schema(description = "Expiry time; empty means never expires")
    private LocalDateTime expired;

    /**
     * Signed in at
     */
    @Schema(description = "Signed in at")
    private LocalDateTime loginTime;

    /**
     * Roles
     */
    @Schema(description = "Roles")
    private Collection<PmsRoleDTO> roles;

    /**
     * Assigned departments ID
     */
    @Schema(description = "Department ID")
    private Set<Long> haveDeptIds;

    /**
     * Assigned tenants ID
     */
    @Schema(description = "Assigned tenants ID")
    private Set<String> haveTenantIds;

    /**
     * Assigned projects ID
     */
    @Schema(description = "Assigned projects ID")
    private Set<Long> haveProjectIds;

    /**
     * Permissions
     */
    @Schema(description = "Permissions")
    private Collection<StringGrantedAuthority> authorities;

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }


    @Override
    public String getUsername() {
        return this.username;
    }


    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        if (expired == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(expired);
    }


    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return status == StatusEnum.NORMAL;
    }


    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String getName() {
        return this.loginId;
    }


}
