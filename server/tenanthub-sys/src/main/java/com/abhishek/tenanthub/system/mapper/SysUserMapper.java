package com.abhishek.tenanthub.system.mapper;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

public interface SysUserMapper {


    // Query the tenants linked to a platform user
    @Select("select tenant_id from sys_user_tenant WHERE user_id = #{userId}")
    Set<String> selectHaveTenants(Long userId);


    // Remove tenants linked to a platform user
    @Delete("delete from sys_user_tenant WHERE user_id = #{userId}")
    int deleteHaveTenants(Long userId);


    // Remove platform users linked to the tenant
    @Delete("delete from sys_user_tenant WHERE tenant_id = #{tenantId}")
    int deleteHaveSysUser(String tenantId);


    // Link a platform user to tenants
    int insertByTenants(@Param("userId") Long userId,
                        @Param("tenantIds") Set<String> tenantIds);


}
