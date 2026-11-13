package com.abhishek.tenanthub.auth.service.impl;

import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.common.enums.AdminFlag;
import com.abhishek.tenanthub.common.enums.StatusEnum;
import com.abhishek.tenanthub.internal.api.ApiPmsUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;


@Slf4j
@Service
public class PmsUserDetailsService implements UserDetailsService {

    @Resource
    @Lazy
    private ApiPmsUserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var dbUser = userService.getByUsername(username);
        if (dbUser == null) {
            throw new UsernameNotFoundException(String.format("Username %s does not exist", username));
        }
        var result = new JwtLoginUser();
        BeanUtils.copyProperties(dbUser, result);
        result.setStatus(StatusEnum.get(dbUser.getStatus()));
        result.setAdminFlag(AdminFlag.get(dbUser.getAdminFlag()));
        result.setAuthorities(Set.of());
        return result;
    }


}
