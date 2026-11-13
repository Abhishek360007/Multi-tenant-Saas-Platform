package com.abhishek.tenanthub.perms.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.abhishek.tenanthub.common.enums.StatusEnum;
import com.abhishek.tenanthub.common.util.IdUtils;
import com.abhishek.tenanthub.internal.api.ApiPmsUserService;
import com.abhishek.tenanthub.internal.api.ApiSysConfigService;
import com.abhishek.tenanthub.internal.api.ApiSysTenantService;
import com.abhishek.tenanthub.internal.dto.InitUserDTO;
import com.abhishek.tenanthub.internal.dto.PmsUserDTO;
import com.abhishek.tenanthub.perms.entity.PmsDept;
import com.abhishek.tenanthub.perms.entity.PmsRole;
import com.abhishek.tenanthub.perms.entity.PmsUser;
import com.abhishek.tenanthub.perms.mapper.PmsDeptMapper;
import com.abhishek.tenanthub.perms.mapper.PmsRoleMapper;
import com.abhishek.tenanthub.perms.mapper.PmsUserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.encryptPassword;
import static com.abhishek.tenanthub.common.consts.ConfigDataConst.*;
import static com.abhishek.tenanthub.common.enums.AdminFlag.YES;
import static com.abhishek.tenanthub.common.enums.GenderType.MALE;
import static com.abhishek.tenanthub.tenant.util.DelegateUtils.delegate;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 11:47
 */


@Slf4j
@Service
@AllArgsConstructor
public class ApiPmsUserServiceImpl implements ApiPmsUserService {

    private final ApiSysConfigService configService;

    private final ApiSysTenantService tenantService;

    private final PmsUserMapper userMapper;

    private final PmsDeptMapper deptMapper;

    private final PmsRoleMapper roleMapper;


    @Override
    public PmsUserDTO getByUsername(String username) {
        var wrapper = new LambdaQueryWrapper<PmsUser>()
                .eq(PmsUser::getUsername, username);
        return toBuilder(userMapper.selectOne(wrapper));
    }


    @Override
    public PmsUserDTO getByMobile(String mobile) {
        var wrapper = new LambdaQueryWrapper<PmsUser>()
                .eq(PmsUser::getMobile, mobile);
        return toBuilder(userMapper.selectOne(wrapper));
    }


    @Override
    public PmsUserDTO getByEmail(String email) {
        var wrapper = new LambdaQueryWrapper<PmsUser>()
                .eq(PmsUser::getEmail, email);
        return toBuilder(userMapper.selectOne(wrapper));
    }


    @Override
    public PmsUserDTO getByUserId(Long userId) {
        return toBuilder(userMapper.selectById(userId));
    }


    @Override
    public Set<PmsUserDTO> listByUserIds(Set<Long> userIds) {
        var wrapper = new LambdaQueryWrapper<PmsUser>()
                .select(PmsUser::getUserId, PmsUser::getUsername, PmsUser::getAvatar, PmsUser::getNickname)
                .in(PmsUser::getUserId, userIds);
        return userMapper.selectList(wrapper)
                .stream()
                .map(this::toBuilder)
                .collect(Collectors.toSet());
    }


    @Override
    public boolean existByUserId(Long userId) {
        return userMapper.exists(
                new LambdaQueryWrapper<PmsUser>()
                        .eq(PmsUser::getUserId, userId)
        );
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initUserAndRoleAndDept(InitUserDTO po) {
        // 租户默认角色名称
        var roleName = Optional.ofNullable(configService.getValueByKey(DEFAULT_ROLE_NAME.getKey()))
                .orElse(DEFAULT_ROLE_NAME.getValue());
        // 委托，新的租户id。执行初始化数据
        delegate(po.getTenantId(), () -> {

            var pmsDept = new PmsDept()
                    .setDeptId(DEFAULT_DEPT_ID.getValue())
                    .setParentId(0L)
                    .setDeptName(po.getName())
                    .setLeader(po.getAdminNickname())
                    .setLeaderMobile(po.getAdminMobile())
                    .setSort(1L)
                    .setDescription(po.getName())
                    .setAncestors("0");

            // 保存部门
            deptMapper.insert(pmsDept);

            var newRoleName = String.format("%s %s", po.getName(), roleName);
            var pmsRole = new PmsRole()
                    .setRoleId(DEFAULT_ROLE_ID.getValue())
                    .setRoleName(newRoleName)
                    .setSort(1L)
                    .setDescription(newRoleName);
            // 保存角色
            roleMapper.insert(pmsRole);

            var password = encryptPassword(po.getAdminPwd());
            var pmsUser = new PmsUser()
                    .setUserId(IdUtils.getStandaloneId())
                    .setAvatar(po.getLogo())
                    .setUsername(po.getAdminUsername())
                    .setMobile(po.getAdminMobile())
                    .setEmail(po.getAdminEmail())
                    .setNickname(po.getAdminNickname())
                    .setPassword(password)
                    // Initial password was chosen by the platform: must be changed at first login
                    .setPasswordChangeRequired((byte) 1)
                    .setGender(MALE.getCode())
                    .setAdminFlag(YES.getCode())
                    .setStatus(StatusEnum.NORMAL.getCode())
                    .setDeptId(pmsDept.getDeptId())
                    .setRoles(Set.of(pmsRole));
            userMapper.insert(pmsUser);

            // 保存关联
            userMapper.insertByRoles(pmsUser.getUserId(), Set.of(pmsRole.getRoleId()));

            return true;
        });
    }


    private PmsUserDTO toBuilder(PmsUser source) {
        if (source == null) {
            return null;
        }
        var target = new PmsUserDTO();
        BeanUtils.copyProperties(source, target);
        return target;
    }

}
