package com.abhishek.saas.system.api.impl;

import com.abhishek.saas.internal.api.ApiSysUserService;
import com.abhishek.saas.system.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/3/6 10:11
 */


@Slf4j
@Service
@AllArgsConstructor
public class ApiSysUserServiceImpl implements ApiSysUserService {

    private final SysUserService userService;


    @Override
    public Set<String> listHaveTenantIds(Long userId) {
        return userService.listHaveTenantIds(userId);
    }

}
