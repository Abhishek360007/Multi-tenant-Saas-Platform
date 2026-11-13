package com.abhishek.tenanthub.perms.service.impl;

import com.abhishek.tenanthub.internal.api.ApiBillingService;
import org.springframework.beans.factory.ObjectProvider;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.auth.service.UserLoginService;
import com.abhishek.tenanthub.common.enums.AdminFlag;
import com.abhishek.tenanthub.common.enums.StatusEnum;
import com.abhishek.tenanthub.common.enums.UserType;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.util.IdUtils;
import com.abhishek.tenanthub.internal.api.ApiCmsProjectService;
import com.abhishek.tenanthub.internal.api.ApiSysConfigService;
import com.abhishek.tenanthub.internal.api.ApiSysMenuService;
import com.abhishek.tenanthub.internal.dto.PmsRoleDTO;
import com.abhishek.tenanthub.internal.dto.SysMenuDTO;
import com.abhishek.tenanthub.perms.entity.PmsDept;
import com.abhishek.tenanthub.perms.entity.PmsRole;
import com.abhishek.tenanthub.perms.entity.PmsUser;
import com.abhishek.tenanthub.perms.mapper.PmsRoleMapper;
import com.abhishek.tenanthub.perms.mapper.PmsUserMapper;
import com.abhishek.tenanthub.perms.po.UserQueryPO;
import com.abhishek.tenanthub.perms.service.PmsDeptService;
import com.abhishek.tenanthub.perms.service.PmsRoleService;
import com.abhishek.tenanthub.perms.service.PmsUserService;
import com.abhishek.tenanthub.perms.vo.*;
import com.abhishek.tenanthub.tenant.base.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.*;
import static com.abhishek.tenanthub.common.consts.ConfigDataConst.DEFAULT_USER_PASSWORD;
import static com.abhishek.tenanthub.common.consts.MbpConst.CREATE_TIME;
import static com.abhishek.tenanthub.common.enums.AdminFlag.NO;
import static com.abhishek.tenanthub.common.enums.MenuTypeEnum.BUTTON;
import static com.abhishek.tenanthub.common.enums.MenuTypeEnum.MENU;
import static com.abhishek.tenanthub.tenant.util.DelegateUtils.delegate;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:36
 */


@Slf4j
@Service
@AllArgsConstructor
public class PmsUserServiceImpl extends BaseServiceImpl<PmsUserMapper, PmsUser> implements PmsUserService {

    private final ApiSysConfigService configService;

    private final ApiCmsProjectService cmsProjectService;

    private final ApiSysMenuService menuService;

    private final UserLoginService userLoginService;

    private final PmsRoleService roleService;

    private final PmsDeptService deptService;

    private final PmsRoleMapper roleMapper;

    private final JwtTokenService jwtTokenService;

    private final ObjectProvider<ApiBillingService> billingService;


    @Override
    public IPage<PmsUser> pageKeywords(UserQueryPO params) {
        var userParams = new PmsUser();
        if (NumberUtil.isValidNumber(params.getDeptId())) {
            userParams.setDeptId(params.getDeptId());
        }
        if (StringUtils.isNotBlank(params.getKeywords())) {
            userParams.setNickname(params.getKeywords());
        }
        Page<PmsUser> pageRequest = Page.of(params.getPageIndex(), params.getPageSize());
        pageRequest.addOrder(OrderItem.desc(CREATE_TIME));
        IPage<PmsUser> result = baseMapper.selectUserPage(pageRequest, userParams);
        if (params.isIncludeCauu()) {
            reflectionFill(result.getRecords());
        }
        // Includes roles and user details
        if (params.isIncludeRad()) {
            setRoleAndDept(result.getRecords());
        }
        // Includes online status
        var loginUserMap = jwtTokenService.mapLoginUser();
        result.getRecords().forEach(r -> {
            r.setPassword(null);
            // An empty LoginId means offline
            var loginUser = loginUserMap.getOrDefault(r.getUserId(), new JwtLoginUser().setLoginId(StrUtil.EMPTY));
            r.setLoginId(loginUser.getLoginId());
        });
        return result;
    }


    private void setRoleAndDept(Collection<PmsUser> list) {
        if (!list.isEmpty()) {
            var userIds = list.stream().map(PmsUser::getUserId).collect(Collectors.toSet());
            var deptIds = list.stream().map(PmsUser::getDeptId).collect(Collectors.toSet());
            var roleMaps = roleService.listByUserIds(userIds);
            var deptMaps = deptService.listByIds(deptIds).stream().collect(
                    Collectors.toMap(PmsDept::getDeptId, d -> d, (o1, o2) -> o2));
            list.forEach(r -> {
                r.setRoles(roleMaps.get(r.getUserId()));
                r.setDept(deptMaps.get(r.getDeptId()));
            });
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(PmsUser entity) {
        // Subscription plan user limit
        billingService.getObject().checkCanAddUser();
        if (exist(PmsUser::getUsername, entity.getUsername())) {
            throw new BizException(String.format("Username %s already exists", entity.getUsername()));
        }
        if (exist(PmsUser::getMobile, entity.getMobile())) {
            throw new BizException(String.format("Mobile number %s already exists", entity.getMobile()));
        }
        if (exist(PmsUser::getEmail, entity.getEmail())) {
            throw new BizException(String.format("Email %s already exists", entity.getEmail()));
        }
        // If the password is empty
        if (StringUtils.isBlank(entity.getPassword())) {
            var userDefaultPassword = Optional.ofNullable(configService.getValueByKey(DEFAULT_USER_PASSWORD.getKey()))
                    .orElse(DEFAULT_USER_PASSWORD.getValue());
            entity.setPassword(userDefaultPassword);
        }

        // Hash the password
        entity.setPassword(encryptPassword(entity.getPassword()));
        // Password chosen by an administrator: the user must change it at first login
        entity.setPasswordChangeRequired((byte) 1);

        if (entity.getAdminFlag() == null || !isAdminUser()) {
            entity.setAdminFlag(NO.getCode());
        }

        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.NORMAL.getCode());
        }
        entity.setUserId(IdUtils.getStandaloneId());
        var ok = super.save(entity);

        if (entity.getRoles() != null) {
            // Roles
            var roleIds = entity.getRoles().stream().map(PmsRole::getRoleId).collect(Collectors.toSet());
            // Update roles
            updateUserRoles(entity.getUserId(), roleIds);
        }

        return ok;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        var user = this.getById(id);
        if (user == null) {
            throw new BizException(String.format("User with ID %s does not exist", id));
        }
        // Admin users cannot be deleted
        if (Objects.equals(user.getAdminFlag(), AdminFlag.YES.getCode())) {
            throw new BizException(String.format("User %s is an admin and cannot be deleted", user.getNickname()));
        }
        // Delete the user roles
        baseMapper.deleteHaveRoles(user.getUserId());
        return super.removeById(id);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(PmsUser entity) {
        var wrapper1 = new LambdaQueryWrapper<PmsUser>()
                .eq(PmsUser::getUsername, entity.getUsername())
                .ne(PmsUser::getUserId, entity.getUserId());
        if (this.count(wrapper1) > 0) {
            throw new BizException(String.format("Username %s already exists", entity.getUsername()));
        }
        var wrapper2 = new LambdaQueryWrapper<PmsUser>()
                .eq(PmsUser::getMobile, entity.getMobile())
                .ne(PmsUser::getUserId, entity.getUserId());
        if (this.count(wrapper2) > 0) {
            throw new BizException(String.format("Mobile number %s already exists", entity.getMobile()));
        }
        var wrapper3 = new LambdaQueryWrapper<PmsUser>()
                .eq(PmsUser::getEmail, entity.getEmail())
                .ne(PmsUser::getUserId, entity.getUserId());
        if (this.count(wrapper3) > 0) {
            throw new BizException(String.format("Email %s already exists", entity.getEmail()));
        }
        if (entity.getRoles() != null) {
            // Roles
            var roleIds = entity.getRoles().stream().map(PmsRole::getRoleId).collect(Collectors.toSet());
            // Update the existing roles
            updateUserRoles(entity.getUserId(), roleIds);
        }
        entity.setPassword(null);
        // Get the signed-in user by username
        var loginUser = jwtTokenService.getLoginUserByUsername(entity.getUsername());
        if (loginUser != null) {
            var copyOptions = CopyOptions.create();
            copyOptions.setIgnoreNullValue(true);
            BeanUtil.copyProperties(entity, loginUser, copyOptions);
            // Non-admin user: get the linked projects by department ID
            if (loginUser.getUserType() == UserType.TENANT
                    && Objects.equals(entity.getAdminFlag(), AdminFlag.NO.getCode())) {
                // Get the project IDs of the signed-in user
                var haveProjectIds = cmsProjectService.listProjectByDeptId(entity.getDeptId());
                loginUser.setHaveProjectIds(haveProjectIds);
            }
            userLoginService.refreshAuthoritys(loginUser);
        }
        return super.updateById(entity);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateUserRoles(Long userId, Set<Long> roleIds) {
        var dbUser = this.getById(userId);
        if (dbUser == null) {
            throw new BizException(String.format("User %d does not exist", userId));
        }
        // Delete all roles of the user first
        baseMapper.deleteHaveRoles(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }
        // then assign the new roles
        return baseMapper.insertByRoles(userId, roleIds) > 0;
    }


    @Override
    public UserRightsVO getUserRights() {
        // Get the tenant of the signed-in user
        String tenantId = JwtSecurityUtils.getTenantId();
        return delegate(tenantId, () -> {
            // User menu permissions
            var userMenus = new HashMap<Long, SysMenuDTO>();
            // Admins get all permissions
            if (isAdminUser()) {
                if (isMasterUser()) {
                    // Platform admins get all non-tenant menus
                    var menuMap = menuService.listMenuByNonTenant()
                            .stream()
                            .collect(Collectors.toMap(SysMenuDTO::getMenuId, Function.identity(), (oldValue, newValue) -> newValue));
                    userMenus.putAll(menuMap);
                } else {
                    // Tenant admins get all tenant menus
                    var menuMap = menuService.listMenuByTenantId(tenantId)
                            .stream()
                            .collect(Collectors.toMap(SysMenuDTO::getMenuId, Function.identity(), (oldValue, newValue) -> newValue));
                    userMenus.putAll(menuMap);
                }
            } else {
                var loginUser = getLoginUser();
                if (loginUser.getRoles() == null || loginUser.getRoles().isEmpty()) {
                    return new UserRightsVO().setButtons(new HashSet<>()).setMenus(new ArrayList<>());
                }
                // Get the roles of the current user ID
                var roleIds = loginUser.getRoles().stream().map(PmsRoleDTO::getRoleId).collect(Collectors.toSet());
                // Get all menus by role ID
                var menuIds = roleMapper.selectMenuIdByRoleIds(roleIds);
                if (menuIds.isEmpty()) {
                    return new UserRightsVO().setButtons(new HashSet<>()).setMenus(new ArrayList<>());
                }
                var menuMap = menuService.listMenuByMenuIds(menuIds)
                        .stream()
                        .collect(Collectors.toMap(SysMenuDTO::getMenuId, Function.identity(), (oldValue, newValue) -> newValue));
                userMenus.putAll(menuMap);
            }

            // Get menus
            var nodeList = userMenus.values()
                    .stream()
                    .filter(r -> r.getMenuType() == MENU.getCode()).map(r -> {
                        var meta = new MenuMetaVO()
                                .setTitle(r.getMenuName())
                                .setIcon(r.getIcon())
                                .setHidden(r.getVisible() == 0)
                                .setKeepAlive(r.getKeepAlive() == 1);
                        var node = new TreeNode<>(r.getMenuId(), r.getParentId(), r.getPerms(), r.getSort());
                        node.setExtra(
                                Map.of(
                                        "meta", meta,
                                        "component", r.getComponent(),
                                        "path", r.getPath()
                                )
                        );
                        return node;
                    }).collect(Collectors.toList());

            // Turn the menu list into a tree
            var treeMenus = TreeUtil.build(nodeList, 0L);

            // Get all buttons
            var buttons = userMenus.values()
                    .stream()
                    .filter(r -> r.getMenuType() == BUTTON.getCode())
                    .map(r -> new ButtonVO().setPerms(r.getPerms()).setTitle(r.getMenuName()))
                    .collect(Collectors.toSet());

            return new UserRightsVO().setMenus(treeMenus).setButtons(buttons);
        });
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updatePassword(UpdatePasswordVO pwd) {
        // Changing a password switches to the tenant of the signed-in user
        // otherwise a platform user managing another tenant could not find their own account
        // Users can only change their own password (ignore any userId in the request)
        pwd.setUserId(getUserId());
        return delegate(getTenantId(), () -> {
            var sysUser = baseMapper.selectById(pwd.getUserId());
            if (!StringUtils.equals(pwd.getNewPwd(), pwd.getConfirmPwd())) {
                throw new BizException("New password and confirmation do not match");
            }
            // Check that the current password is correct.
            if (matchesPassword(pwd.getOldPwd(), sysUser.getPassword())) {
                // Check whether the new and current passwords are the same
                if (matchesPassword(pwd.getNewPwd(), sysUser.getPassword())) {
                    throw new BizException("New password must differ from the current one");
                } else {
                    // Encrypt the new password
                    var newPassword = encryptPassword(pwd.getNewPwd());
                    // Edit
                    var pmsUser = PmsUser.builder().userId(pwd.getUserId()).password(newPassword)
                            .passwordChangeRequired((byte) 0).build();
                    return super.updateById(pmsUser);
                }
            } else {
                throw new BizException("Current password is incorrect");
            }
        });
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean resetPassword(ResetPasswordVO pwd) {
        var sysUser = baseMapper.selectById(pwd.getUserId());
        if (sysUser != null) {
            // Encrypt the new password
            String newPassword = encryptPassword(pwd.getNewPwd());
            // Edit
            // Reset by an administrator: the user must choose their own password at next login
            var pmsUser = new PmsUser().setUserId(pwd.getUserId()).setPassword(newPassword)
                    .setPasswordChangeRequired((byte) 1);
            return super.updateById(pmsUser);
        }
        throw new BizException("User does not exist; cannot reset the password");
    }


}
