package com.abhishek.tenanthub.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.internal.api.ApiPmsUserService;
import com.abhishek.tenanthub.system.entity.SysTenant;
import com.abhishek.tenanthub.system.mapper.SysTenantMapper;
import com.abhishek.tenanthub.system.mapper.SysUserMapper;
import com.abhishek.tenanthub.system.service.SysUserService;
import com.abhishek.tenanthub.system.vo.UserListTenantVO;
import com.abhishek.tenanthub.tenant.service.MultiTenantManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.*;
import static com.abhishek.tenanthub.tenant.util.DelegateUtils.delegate;


@Slf4j
@Service
@AllArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper baseMapper;

    private final ApiPmsUserService pmsUserService;

    private final MultiTenantManager tenantManager;

    private final SysTenantMapper tenantMapper;

    private final JwtTokenService jwtTokenService;


    @Override
    public Set<String> listHaveTenantIds(Long userId) {
        return delegate(tenantManager.getDefaultTenantId(),
                () -> baseMapper.selectHaveTenants(userId)
        );
    }


    @Override
    public UserListTenantVO listHaveTenants(Long userId) {
        var tenantIds = listHaveTenantIds(userId);
        var wrapper = new LambdaQueryWrapper<SysTenant>()
                .select(
                        SysTenant::getTenantId,
                        SysTenant::getName
                );
        var tenants = tenantMapper.selectList(wrapper)
                .stream()
                .map(r -> new UserListTenantVO.Entry(r.getTenantId(), r.getName()))
                .collect(Collectors.toSet());
        return new UserListTenantVO()
                .setHave(tenantIds)
                .setAll(tenants);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTenant(Long userId, Set<String> tenantIds) {
        // Only platform admins can assign tenants to other users
        if (isMasterUser() && isAdminUser()) {
            // Platform admins need no assignment; they see every tenant
            if (Objects.equals(getUserId(), userId)) {
                return true;
            }
            // Get the existing tenants ID
            var collect = tenantIds.stream().filter(tenantManager::existById).collect(Collectors.toSet());
            return delegate(tenantManager.getDefaultTenantId(), () -> {
                var sysUser = pmsUserService.getByUserId(userId);
                // Check that the platform user exists
                if (sysUser == null) {
                    throw new BizException(String.format("Platform user %s does not exist", userId));
                }
                // Delete the tenants previously linked to the platform user
                baseMapper.deleteHaveTenants(userId);
                // Get the signed-in user by username
                var loginUser = jwtTokenService.getLoginUserByUsername(sysUser.getUsername());
                if (loginUser != null) {
                    // Update the tenants linked to the platform user ID
                    loginUser.setHaveTenantIds(collect);
                    jwtTokenService.updateLoginUser(loginUser);
                }
                if (!collect.isEmpty()) {
                    // Link the platform user to the new tenants
                    var r2 = baseMapper.insertByTenants(userId, collect);
                    return r2 > 0;
                }
                return true;
            });
        }
        throw new BizException("Only platform admins can assign tenants to platform users");
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteHaveSysUser(String tenantId) {
        return baseMapper.deleteHaveSysUser(tenantId) > 0;
    }


}
