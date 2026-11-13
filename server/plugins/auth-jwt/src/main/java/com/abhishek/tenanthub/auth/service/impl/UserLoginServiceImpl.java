package com.abhishek.tenanthub.auth.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.abhishek.tenanthub.auth.enums.GrantType;
import com.abhishek.tenanthub.auth.exception.JwtAuthException;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.auth.jwt.JwtTokenValue;
import com.abhishek.tenanthub.auth.jwt.StringGrantedAuthority;
import com.abhishek.tenanthub.auth.po.LoginFormPO;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.auth.service.LineCaptchaService;
import com.abhishek.tenanthub.auth.service.RsaAsymmetricCryptoService;
import com.abhishek.tenanthub.auth.service.UserLoginService;
import com.abhishek.tenanthub.common.domain.CustomRequestInfo;
import com.abhishek.tenanthub.common.enums.AdminFlag;
import com.abhishek.tenanthub.common.enums.StatusEnum;
import com.abhishek.tenanthub.common.enums.UserType;
import com.abhishek.tenanthub.common.util.IdUtils;
import com.abhishek.tenanthub.common.util.ServletUtils;
import com.abhishek.tenanthub.internal.api.*;
import com.abhishek.tenanthub.internal.dto.LoginLogDTO;
import com.abhishek.tenanthub.internal.dto.PmsRoleDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.common.consts.DataScopeConst.*;

/**
 * <p>
 * User login
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @version 1.0.1
 * @createTime 2020/3/5 0005 11:32
 */

@Slf4j
@Service
@AllArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final AuthenticationManager authManager;

    private final JwtTokenService tokenService;

    private final ApiLogStorageService logStorageService;

    public final LineCaptchaService captchaService;

    private final ApiPmsRoleService roleService;

    private final ApiPmsDeptService deptService;

    private final ApiSysMenuService menuService;

    private final ApiSysTenantService tenantService;

    private final ApiSysUserService sysUserService;

    private final ApiCmsProjectService cmsProjectService;

    private final RsaAsymmetricCryptoService cryptoService;

    /**
     * From the sign-in form Token
     *
     * @return
     * @date 2018/11/16
     * @author Wang Chen Chen
     */
    @Override
    public JwtTokenValue login(LoginFormPO po, HttpServletRequest request) throws JwtAuthException {
        // Whether the captcha is correct
        if (!captchaService.check(po.getCodeKey(), po.getCodeText())) {
            throw new JwtAuthException("Authentication failed: incorrect captcha.");
        }
        // Check whether a tenant exists
        var currentTenant = tenantService.getByCurrentTenant();
        if (currentTenant == null) {
            throw new JwtAuthException("Tenant does not exist");
        }
        // Check the tenant status
        if (!Objects.equals(currentTenant.getStatus(), StatusEnum.NORMAL.getCode())) {
            throw new JwtAuthException(String.format("Tenant %s is disabled", currentTenant.getName()));
        }
        // Check whether the tenant has expired
        if (LocalDateTime.now().isAfter(currentTenant.getExpired())) {
            var format = LocalDateTimeUtil.formatNormal(currentTenant.getExpired().toLocalDate());
            throw new JwtAuthException(String.format("Tenant %s expired on %s", currentTenant.getName(), format));
        }
        // Decrypt
        final var username = cryptoService.decrypt(po.getUsername()).trim();
        po.setUsername(username);
        final var password = cryptoService.decrypt(po.getPassword()).trim();
        // Wrap the submitted username and password in a UsernamePasswordAuthenticationToken
        var authResult = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var target = new JwtLoginUser();
        BeanUtils.copyProperties(authResult.getPrincipal(), target);
        target.setGrantType(GrantType.PASSWORD);
        target.setLoginTime(LocalDateTime.now());
        target.setTenantId(currentTenant.getTenantId());
        // Generate a random ID linked to the current user
        target.setLoginId(IdUtils.getStandaloneStrId());

        // Determine whether the signed-in user is a platform user or a tenant user
        String defaultTenantId = tenantService.getByDefaultTenantId();
        var userType = StringUtils.equals(defaultTenantId, target.getTenantId())
                ? UserType.SYSTEM : UserType.TENANT;
        target.setUserType(userType);

        target.setHaveProjectIds(new HashSet<>());
        target.setHaveTenantIds(new HashSet<>());

        // Whether the signed-in user is a platform user
        if (userType == UserType.SYSTEM) {
            var haveTenantIds = sysUserService.listHaveTenantIds(target.getUserId());
            target.setHaveTenantIds(haveTenantIds);
        } else {
            // Non-admin user: get the linked projects by department ID
            if (target.getAdminFlag() == AdminFlag.NO) {
                // Get the project IDs of the signed-in user
                var haveProjectIds = cmsProjectService.listProjectByDeptId(target.getDeptId());
                target.setHaveProjectIds(haveProjectIds);
            }
        }

        // Set role and menu permissions
        setAuthoritys(target);

        tokenService.setLoginUser(target);

        // Save the login log to the database
        asyncLoginLogSave(target, ServletUtils.getRequestInfo(request));

        // Delete the captcha from Redis
        captchaService.delete(po.getCodeKey());

        var props = tokenService.getProps();

        // Mark the current user as signed in
        JwtSecurityUtils.setLoginUser(target);

        return JwtTokenValue.builder()
                .header(props.getTokenHeader())
                .accessToken(tokenService.createJwtStr(target.getLoginId()))
                .tokenType(props.getTokenType())
                .expiresIn(props.getTokenExpired())
                .build();
    }


    @Override
    public void refreshAuthoritys() {
        // Check whether the user is signed in
        if (JwtSecurityUtils.isAuthenticated()) {
            refreshAuthoritys(JwtSecurityUtils.getLoginUser());
        }
    }


    @Override
    public void refreshAuthoritys(JwtLoginUser target) {
        target.setAuthorities(List.of());
        // Set the user permissions
        setAuthoritys(target);
        // Update the user permissions
        tokenService.updateLoginUser(target);
    }


    /**
     * Set the user permissions
     *
     * @author WangChenChen
     * @date 2023/2/6 15:56
     */
    private void setAuthoritys(JwtLoginUser target) {
        // Get the roles of the signed-in user
        var roles = roleService.listByUserId(target.getUserId());
        if (roles.isEmpty()) {
            target.setRoles(new HashSet<>());
            return;
        }
        target.setRoles(roles);

        // Get department permissions from the roles
        var deptIds = listDeptIdByRoles(target);
        target.setHaveDeptIds(deptIds);

        // Whether the signed-in user is an admin
        // Platform admin: get every menu except tenant menus.
        // Tenant admin: get all menus from the tenant templates
        if (target.getAdminFlag() == AdminFlag.YES) {
            // Whether the signed-in user is a platform user
            Set<StringGrantedAuthority> authorities;
            if (target.getUserType() == UserType.SYSTEM) {
                authorities = menuService.listPermsByNonTenant()
                        .stream()
                        .map(StringGrantedAuthority::new)
                        .collect(Collectors.toSet());
            } else {
                authorities = menuService.listPermsByTenantId(target.getTenantId())
                        .stream()
                        .map(StringGrantedAuthority::new)
                        .collect(Collectors.toSet());
            }
            target.setAuthorities(authorities);
            return;
        }
        // Non-admin: get the permissions linked to the roles
        var roleIds = roles.stream().map(PmsRoleDTO::getRoleId).collect(Collectors.toSet());

        // then get the menus linked to the roles ID
        var menuIds = roleService.listMenuIdByRoleIds(roleIds);
        if (!menuIds.isEmpty()) {
            var authorities = menuService.listPermsByMenuIds(menuIds)
                    .stream()
                    .map(StringGrantedAuthority::new)
                    .collect(Collectors.toSet());
            target.setAuthorities(authorities);
        }
    }


    public Set<Long> listDeptIdByRoles(JwtLoginUser target) {
        if (target.getUserType() == UserType.SYSTEM || target.getAdminFlag() == AdminFlag.YES) {
            return deptService.listDeptIdByAll();
        }
        if (target.getRoles().isEmpty()) {
            return Set.of();
        }
        // Whether the roles include 1. all data
        var isAll = target.getRoles().stream().anyMatch(a -> Objects.equals(a.getDataScope(), DATA_SCOPE_ALL));
        if (isAll) {
            return deptService.listDeptIdByAll();
        }

        var depsIds = new HashSet<Long>();
        // Whether the roles include 2. custom data
        var roleIds = target.getRoles().stream()
                .filter(a -> Objects.equals(a.getDataScope(), DATA_SCOPE_CUSTOM))
                .map(PmsRoleDTO::getRoleId).collect(Collectors.toSet());
        if (!roleIds.isEmpty()) {
            var v1 = deptService.listDeptIdByRuleId(roleIds);
            depsIds.addAll(v1);
        }

        // Whether the roles include 3. own department only
        var isOnlyMe = target.getRoles().stream().anyMatch(a -> Objects.equals(a.getDataScope(), DATA_SCOPE_DEPT));
        if (isOnlyMe) {
            depsIds.add(target.getDeptId());
        }

        // Whether the roles include 4. own department and below
        var isMeAndChild = target.getRoles().stream().anyMatch(a -> Objects.equals(a.getDataScope(), DATA_SCOPE_DEPT_AND_CHILD));
        if (isMeAndChild) {
            var v1 = deptService.listChildIdByDeptId(target.getDeptId());
            depsIds.addAll(v1);
        }

        return depsIds;
    }


    /**
     * Save the login log
     *
     * @author WangChenChen
     * @date 2023/2/6 15:56
     */
    private void asyncLoginLogSave(JwtLoginUser loginUser, CustomRequestInfo request) {
        var loginLog = new LoginLogDTO();
        BeanUtils.copyProperties(loginUser, loginLog);
        loginLog.setId(loginUser.getLoginId());
        loginLog.setRequestUrl(request.getRequestUrl());
        loginLog.setRequestIp(request.getIp());
        loginLog.setAddress(request.getAddress());
        loginLog.setOsName(request.getOsName());
        loginLog.setBrowser(request.getBrowser());
        loginLog.setGrantType(loginUser.getGrantType().getCode());
        loginLog.setCreateTime(loginUser.getLoginTime());
        logStorageService.asyncLoginSave(loginLog);
    }


}
